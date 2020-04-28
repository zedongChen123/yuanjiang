package com.checc.system.controller;

import cn.hutool.core.convert.Convert;
import com.checc.common.constant.UserConstants;
import com.checc.common.core.controller.BaseController;
import com.checc.common.core.domain.AjaxResult;
import com.checc.common.core.domain.R;
import com.checc.common.core.page.TableDataInfo;
import com.checc.common.log.annotation.OperLog;
import com.checc.common.log.enums.BusinessType;
import com.checc.common.security.LoginUser;
import com.checc.common.security.annotation.HasPermission;
import com.checc.common.security.service.TokenService;
import com.checc.common.security.utils.SecurityUtils;
import com.checc.common.utils.ServletUtils;
import com.checc.common.utils.StringUtils;
import com.checc.common.utils.poi.ExcelUtil;
import com.checc.system.domain.SysUser;
import com.checc.system.service.ISysPostService;
import com.checc.system.service.ISysRoleService;
import com.checc.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * 用户信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取用户列表
     */
    @HasPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @OperLog(title = "用户管理", businessType = BusinessType.EXPORT)
    @HasPermission("system:user:export")
    @GetMapping("/export")
    public R export(SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @OperLog(title = "用户管理", businessType = BusinessType.IMPORT)
    @HasPermission("system:user:import")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @GetMapping("/importTemplate")
    public R importTemplate()
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 根据用户编号获取详细信息
     */
    @HasPermission("system:user:query")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        AjaxResult ajax = AjaxResult.success();
        ajax.put("roles", roleService.selectRoleAll());
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            ajax.put(AjaxResult.DATA_TAG, userService.selectUserById(userId));
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @HasPermission("system:user:add")
    @OperLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysUser user)
    {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName())))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @HasPermission("system:user:edit")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return AjaxResult.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @HasPermission("system:user:remove")
    @OperLog(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @HasPermission("system:user:edit")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @HasPermission("system:user:edit")
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 远程调用实现方法
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    @GetMapping("/find/{username}")
    public SysUser selectUserByUserName(@PathVariable("username") String username){
        return userService.selectUserByUserName(username);
    }

    /**
     * 远程调用方法
     * @param userId
     * @return
     */
    @GetMapping("/get/{userId}")
    public SysUser selectSysUserByUserId(@PathVariable("userId") Long userId){
        return userService.selectUserById(userId);
    }

    @PostMapping("/update/login")
    public AjaxResult updateUserLoginRecord(@RequestBody SysUser user){
        return toAjax(userService.updateUser(user));
    }

    @GetMapping("/hasRoles")
    public Set<Long> hasRoles(@RequestParam("roleIds") String roleIds){
        Long[] arr= Convert.toLongArray(roleIds);
        return userService.selectUserIdsHasRoles(arr);
    }

    /**
     * 查询所有当前部门中的用户
     */
    @GetMapping("/inDepts")
    public Set<Long> inDept(@RequestParam("deptIds") String deptIds)
    {
        Long[] arr=Convert.toLongArray(deptIds);
        return userService.selectUserIdsInDepts(arr);
    }


}
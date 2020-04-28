package com.checc.system.feign;

import com.checc.common.constant.ServiceNameConstants;
import com.checc.common.core.domain.AjaxResult;
import com.checc.common.core.domain.R;
import com.checc.system.config.FeignConfig;
import com.checc.system.domain.SysUser;
import com.checc.system.feign.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 用户 Feign服务层
 * 
 * @author checc
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class, configuration = FeignConfig.class)
public interface RemoteUserService
{
    @GetMapping("/system/user/get/{userId}")
    public SysUser selectSysUserByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/system/user/find/{username}")
    public SysUser selectUserByUserName(@PathVariable("username") String username);

    @PostMapping("/system/user/update/login")
    public AjaxResult updateUserLoginRecord(@RequestBody SysUser user);

    /**
     * 查询拥有当前角色的所有用户
     * @param roleIds
     * @return
     * @author checc
     */
    @GetMapping("/system/user/hasRoles")
    public Set<Long> selectUserIdsHasRoles(@RequestParam("roleIds") String roleIds);

    /**
     * 查询所有当前部门中的用户
     * 
     * @param deptIds
     * @return
     * @author checc
     */
    @GetMapping("/system/user/inDepts")
    public Set<Long> selectUserIdsInDepts(@RequestParam("deptIds") String deptIds);
}

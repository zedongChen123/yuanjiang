package com.checc.system.feign;

import com.checc.common.constant.ServiceNameConstants;
import com.checc.system.domain.SysMenu;
import com.checc.system.feign.factory.RemoteMenuFallbackFactory;
import com.checc.system.vo.RouterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * 菜单 Feign服务层
 * 
 * @author checc
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteMenuFallbackFactory.class)
public interface RemoteMenuService
{
    @GetMapping("/system/menu/perms/{userId}")
    public Set<String> selectMenuPermsByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/system/menu/tree/{userId}")
    public List<SysMenu> selectMenuTreeByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/system/menu/build")
    public List<RouterVo> buildMenus(@RequestParam("menus") List<SysMenu> menus);
}

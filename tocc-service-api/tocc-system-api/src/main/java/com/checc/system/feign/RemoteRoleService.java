package com.checc.system.feign;

import com.checc.common.constant.ServiceNameConstants;
import com.checc.system.feign.factory.RemoteRoleFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

/**
 * 角色 Feign服务层
 * 
 * @author checc
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteRoleFallbackFactory.class)
public interface RemoteRoleService
{
    @GetMapping("/system/role/get/{roleId}")
    public Set<String> selectRolePermissionByUserId(@PathVariable("roleId") Long roleId);
}

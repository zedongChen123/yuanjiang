package com.checc.system.feign.factory;

import com.checc.system.domain.SysRole;
import com.checc.system.feign.RemoteRoleService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class RemoteRoleFallbackFactory implements FallbackFactory<RemoteRoleService>
{
    @Override
    public RemoteRoleService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteRoleService()
        {
            @Override
            public Set<String> selectRolePermissionByUserId(Long roleId) {
                return null;
            }
        };
    }
}

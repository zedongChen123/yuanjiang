package com.checc.system.feign.factory;

import com.checc.common.core.domain.AjaxResult;
import com.checc.common.core.domain.R;
import com.checc.system.domain.SysUser;
import com.checc.system.feign.RemoteUserService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService>
{
    @Override
    public RemoteUserService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteUserService()
        {
            @Override
            public AjaxResult updateUserLoginRecord(SysUser user)
            {
                return AjaxResult.error();
            }


            @Override
            public SysUser selectSysUserByUserId(Long userId) {
                SysUser user = new SysUser();
                user.setUserId(0l);
                user.setUserName("no user");
                return user;
            }

            @Override
            public SysUser selectUserByUserName(String username) {
                return null;
            }

            @Override
            public Set<Long> selectUserIdsHasRoles(String roleId)
            {
                return null;
            }

            @Override
            public Set<Long> selectUserIdsInDepts(String deptIds)
            {
                return null;
            }
        };
    }
}

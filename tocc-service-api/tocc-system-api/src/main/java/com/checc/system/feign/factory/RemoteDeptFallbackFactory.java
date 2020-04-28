package com.checc.system.feign.factory;

import com.checc.system.domain.SysDept;
import com.checc.system.feign.RemoteDeptService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteDeptFallbackFactory implements FallbackFactory<RemoteDeptService>
{

    @Override
    public RemoteDeptService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteDeptService()
        {

            @Override
            public SysDept selectSysDeptByDeptId(Long deptId) {
                return null;
            }

        };
    }
}

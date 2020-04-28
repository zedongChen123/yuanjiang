package com.checc.system.feign.factory;

import com.checc.system.domain.SysMenu;
import com.checc.system.feign.RemoteMenuService;
import com.checc.system.vo.RouterVo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class RemoteMenuFallbackFactory implements FallbackFactory<RemoteMenuService>
{
    @Override
    public RemoteMenuService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteMenuService()
        {

            @Override
            public Set<String> selectMenuPermsByUserId(Long userId) {
                return null;
            }

            @Override
            public List<SysMenu> selectMenuTreeByUserId(Long userId) {
                return null;
            }

            @Override
            public List<RouterVo> buildMenus(List<SysMenu> menus) {
                return null;
            }
        };
    }
}

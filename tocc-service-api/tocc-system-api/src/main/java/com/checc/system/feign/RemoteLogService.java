package com.checc.system.feign;

import com.checc.common.constant.ServiceNameConstants;
import com.checc.system.domain.SysLogininfor;
import com.checc.system.domain.SysOperLog;
import com.checc.system.feign.factory.RemoteLogFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 日志Feign服务层
 * 
 * @author checc
 * @date 2019-05-20
 */
@FeignClient(name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService
{
    @PostMapping("/monitor/operlog/save")
    public void insertOperlog(@RequestBody SysOperLog operLog);

    @PostMapping("/monitor/logininfor/save")
    public void insertLoginlog(@RequestBody SysLogininfor logininfor);
}

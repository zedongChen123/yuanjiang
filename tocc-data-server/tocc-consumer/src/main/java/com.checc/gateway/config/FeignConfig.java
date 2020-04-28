package com.checc.gateway.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * openFeign 日志配置
 */
@Configuration
public class FeignConfig {

    /**
     * NONE:不记录任何日志（默认值）
     * BASIC:仅记录请求方法、URL、响应状态码以及执行时间
     * HEADERS:记录BASIC级别的基础上，记录请求响应的header
     * FULL:记录请求和响应的header，body和元数据
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}

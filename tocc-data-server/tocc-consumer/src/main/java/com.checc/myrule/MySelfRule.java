package com.checc.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MySelfRule {
    /**
     * 自定义轮询配置方案
     */
    @Bean
    public IRule myRule(){
        return new RandomRule(); //随机
    }
}

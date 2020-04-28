package com.checc.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 启动程序
 */
@EnableHystrix
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages = {"com.checc"})
@EnableEurekaClient
@EnableFeignClients("com.checc")
public class ToccAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ToccAuthApplication.class, args);
    }
}
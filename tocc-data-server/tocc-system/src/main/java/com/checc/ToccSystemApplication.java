package com.checc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 
 * @author checc
 */
@MapperScan("com.checc.*.mapper")
@EnableFeignClients("com.checc")
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class },scanBasePackages = {"com.checc"})
public class ToccSystemApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ToccSystemApplication.class, args);
    }
}

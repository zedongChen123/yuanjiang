package com.checc.gateway;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

@Import(FdfsClientConfig.class)
@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {"com.checc"})
public class PaymentMainCopy {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMainCopy.class,args);
    }
}

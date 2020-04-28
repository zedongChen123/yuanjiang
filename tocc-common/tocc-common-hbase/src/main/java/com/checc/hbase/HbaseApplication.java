package com.checc.hbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Desc 描述信息
 * @Auther: zhangwenhao
 * @Date: 2020/4/24
 * @Version: 1.0
 * @Last Modified by: zhangwenhao
 * @Last Modified time: 2020/4/24
 */
@SpringBootApplication(scanBasePackages = {"com.checc"})
public class HbaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(HbaseApplication.class, args);
    }
}

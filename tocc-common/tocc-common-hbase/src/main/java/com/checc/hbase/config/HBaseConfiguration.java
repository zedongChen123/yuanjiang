package com.checc.hbase.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @Desc 描述信息
 * @Auther: zhangwenhao
 * @Date: 2020/4/24
 * @Version: 1.0
 * @Last Modified by: zhangwenhao
 * @Last Modified time: 2020/4/24
 */
@Slf4j
@Configuration
//@Component
public class HBaseConfiguration {

    @Value("${hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String clientPort;

    @Bean
    public Connection connection() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
        conf.set("hbase.zookeeper.property.clientPort", clientPort);
//        conf.set("zookeeper.znode.parent", "");
        try {
            return ConnectionFactory.createConnection(conf);
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }
}

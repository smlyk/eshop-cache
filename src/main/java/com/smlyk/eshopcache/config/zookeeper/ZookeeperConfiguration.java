package com.smlyk.eshopcache.config.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: always
 * @Date: 2021/1/12 3:23 下午
 */
@Configuration
public class ZookeeperConfiguration {

    @Autowired
    private ZookeeperProperties zkProperties;

    @Bean(initMethod = "start")//对象创建完成后,调用start方法
    public CuratorFramework curatorFramework(){
        return CuratorFrameworkFactory.builder()
                .connectString(zkProperties.getConnectString())
                .sessionTimeoutMs(zkProperties.getSessionTimeoutMs())
                .connectionTimeoutMs(zkProperties.getConnectionTimeoutMs())
                .retryPolicy(new RetryNTimes(zkProperties.getRetryCount(), zkProperties.getElapsedTimeMs()))
                .build();
    }


}

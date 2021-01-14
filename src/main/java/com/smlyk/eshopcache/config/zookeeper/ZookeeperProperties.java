package com.smlyk.eshopcache.config.zookeeper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: always
 * @Date: 2021/1/12 3:20 下午
 */
@Component
@ConfigurationProperties(prefix = "curator")
@Data
public class ZookeeperProperties {

    private int retryCount;

    private int elapsedTimeMs;

    private String connectString;

    private int sessionTimeoutMs;

    private int connectionTimeoutMs;

}

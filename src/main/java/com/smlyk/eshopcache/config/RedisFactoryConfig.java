package com.smlyk.eshopcache.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: always
 * @Date: 2020/12/29 4:33 下午
 */
@Configuration
public class RedisFactoryConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.cluster.timeout}")
    private String clusterTimeout;

    @Value("${spring.redis.cluster.max-redirects}")
    private String clusterMaxRedirects;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        Map<String, Object> source = new HashMap<>();
        source.put("spring.redis.cluster.nodes", clusterNodes);
        source.put("spring.redis.cluster.timeout", clusterTimeout);
        source.put("spring.redis.cluster.max-redirects", clusterMaxRedirects);
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
        return new LettuceConnectionFactory(redisClusterConfiguration);
    }


}

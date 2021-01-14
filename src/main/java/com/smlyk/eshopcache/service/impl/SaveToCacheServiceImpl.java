package com.smlyk.eshopcache.service.impl;

import com.alibaba.fastjson.JSON;
import com.smlyk.eshopcache.controller.UserController;
import com.smlyk.eshopcache.dto.ProductInfo;
import com.smlyk.eshopcache.service.ISaveToCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;

import static java.util.Objects.nonNull;

/**
 * @Author: always
 * @Date: 2021/1/6 5:37 下午
 */
@Service
@Slf4j
public class SaveToCacheServiceImpl implements ISaveToCacheService {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Override
    @CachePut(value = UserController.CACHE_NAME, key = "'productInfo_' + #productInfo.getId()")
    public ProductInfo saveToLocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    @Cacheable(value = UserController.CACHE_NAME, key = "'productInfo_' + #id")
    public ProductInfo getFromLocalCache(Integer id) {
        return null;
    }

    @Override
    public void saveToRedisCache(ProductInfo productInfo) {

        String key = "productInfo_" + productInfo.getId();
        redisTemplate.opsForValue().set(key, productInfo);
    }

    @Override
    public ProductInfo getFromRedisCache(String key) {
        return (ProductInfo) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void rebuildCache(ProductInfo productInfo) {
        //1.先从redis中获取数据，如果为空，直接将获取的数据写进redis和本地缓存。
        //2.如果不为空，则需要比较本次更新的时间和redis中已有数据的更新时间，如果比redis中新，则更新redis和本地缓存；否则直接返回获取的数据
        String key = "productInfo_" + productInfo.getId();
        ProductInfo productInfoCache = getFromRedisCache(key);
        if (nonNull(productInfoCache)){
            log.info("缓存中更新时间为："+ productInfoCache.getModifiedTime() + "; 需要更新的更新时间为：" + productInfo.getModifiedTime());
            if (productInfo.getModifiedTime().compareTo(productInfoCache.getModifiedTime()) < 0) return;
        }

        //方便测试，睡眠15秒
        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //保存到本地缓存和redis缓存中
        saveToLocalCache(productInfo);
        saveToRedisCache(productInfo);
    }
}

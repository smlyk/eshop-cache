package com.smlyk.eshopcache.service;

import com.smlyk.eshopcache.dto.ProductInfo;

/**
 * @Author: always
 * @Date: 2021/1/6 5:36 下午
 */
public interface ISaveToCacheService {

    ProductInfo saveToLocalCache(ProductInfo productInfo);

    ProductInfo getFromLocalCache(Integer id);

    void saveToRedisCache(ProductInfo productInfo);

    ProductInfo getFromRedisCache(String key);

    /**
     * 重建缓存
     * @param productInfo
     */
    void rebuildCache(ProductInfo productInfo);

}

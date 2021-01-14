package com.smlyk.eshopcache.controller;

import com.alibaba.fastjson.JSON;
import com.smlyk.eshopcache.dto.ProductInfo;
import com.smlyk.eshopcache.dto.ShopInfo;
import com.smlyk.eshopcache.rebuild.RebuildCacheQueue;
import com.smlyk.eshopcache.service.ISaveToCacheService;
import com.smlyk.eshopcache.zookeeper.ZkDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: always
 * @Date: 2021/1/12 1:58 下午
 */
@RestController
@RequestMapping
@Slf4j
public class CacheController {

    @Autowired
    private ISaveToCacheService cacheService;

    @Autowired
    private ZkDistributedLock zkDistributedLock;

    @GetMapping("getProductInfo")
    public ProductInfo getProductInfo(Integer productId){

        ProductInfo productInfo = null;

        String key = "productInfo_" + productId;
        productInfo = cacheService.getFromRedisCache(key);
       log.info("=================从redis中获取缓存，商品信息=" + JSON.toJSONString(productInfo));

        if (productInfo == null){
            productInfo = cacheService.getFromLocalCache(productId);
            log.info("=================从ehcache中获取缓存，商品信息=" + productInfo);
        }

        if(productInfo == null) {
            //需要从数据源重新拉去数据，重建缓存

            //模拟获取到的数据
            productInfo = new ProductInfo(1010, "Iphone12", "玫瑰金", 7999.00, "aaa.jpg,bbb.jpg",
                    "2000g", "全年保修", "5.7寸" ,19, "2021-01-12 17:01:00");
            String path = null;

            try {
                //现获取分布式锁
                path = "productInfo_lock_" + productId;
                zkDistributedLock.acquireDistributedLock(path);
                //获取到了锁
                //执行一系列操作：1.先从redis中获取数据，如果为空，直接将获取的数据写进redis和本地缓存。
                //2.如果不为空，则需要比较本次更新的时间和redis中已有数据的更新时间，如果比redis中新，则更新redis和本地缓存；否则直接返回获取的数据
                //由于这个操作时间比较长，所以需要创建一个队列将任务放在队列中，创建一个线程从队列中阻塞式获取任务来处理
                RebuildCacheQueue.getInstance().putProductInfo(productInfo);
            } catch (InterruptedException e) {
                log.error("rebuildCache error:{}", e);
            } finally {
                //释放锁
                zkDistributedLock.releaseDistributedLock(path);
            }
        }
        return productInfo;
    }

    @GetMapping("getShopInfo")
    public ShopInfo getShopInfo(Integer shopId){
        return new ShopInfo(20, "苹果官方旗舰店", 5, 99.0);
    }


}

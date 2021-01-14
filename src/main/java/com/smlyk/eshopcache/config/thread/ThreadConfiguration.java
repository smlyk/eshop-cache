package com.smlyk.eshopcache.config.thread;

import com.smlyk.eshopcache.dto.ProductInfo;
import com.smlyk.eshopcache.rebuild.RebuildCacheQueue;
import com.smlyk.eshopcache.service.ISaveToCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: always
 * @Date: 2021/1/14 10:24 上午
 */
@Configuration
@Slf4j
public class ThreadConfiguration {

    @Autowired
    private ISaveToCacheService saveToCacheService;


    @Bean(initMethod = "start")
    public Thread thread(){
        return new Thread(() -> {
            //重建缓存队列
            RebuildCacheQueue queue = RebuildCacheQueue.getInstance();

            //循环阻塞式的从队列获取数据来进行缓存重建操作
            while (true){
                try {
                    ProductInfo productInfo = queue.takeProductInfo();
                    //重建缓存
                    saveToCacheService.rebuildCache(productInfo);
                } catch (Exception e) {
                    log.error("重建缓存线程执行异常:{}", e);
                }
            }
        });
    }
}

package com.smlyk.eshopcache.rebuild;

import com.smlyk.eshopcache.dto.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重建缓存的内存队列(单例)
 * @Author: always
 * @Date: 2021/1/12 5:25 下午
 */
public class RebuildCacheQueue {

    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<>(100);

    /**
     * 放入队列
     * @param productInfo
     * @throws InterruptedException
     */
    public void putProductInfo(ProductInfo productInfo) throws InterruptedException {
        queue.put(productInfo);
    }

    /**
     * 消费队列
     * @return
     * @throws InterruptedException
     */
    public ProductInfo takeProductInfo() throws InterruptedException {
        return queue.take();
    }

    private RebuildCacheQueue(){
    }

    public static RebuildCacheQueue getInstance(){
        return Singleton.getInstance();
    }

    public static void init(){
        getInstance();
    }

    private static class Singleton{

        private static RebuildCacheQueue instance;

        static {
            instance = new RebuildCacheQueue();
        }

        private static RebuildCacheQueue getInstance(){
            return instance;
        }
    }

}

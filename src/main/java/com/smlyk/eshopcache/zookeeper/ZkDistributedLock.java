package com.smlyk.eshopcache.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

/**
 * @Author: always
 * @Date: 2021/1/12 3:37 下午
 */
@Slf4j
@Service
public class ZkDistributedLock implements InitializingBean {

    private final static String ROOT_PATH_LOCK = "rootLock";
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Autowired
    private CuratorFramework curatorFramework;

    /**
     * 获取分布式锁
     * @param path
     */
    public void acquireDistributedLock(String path){
        String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        while (true){
            try {
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(keyPath);
                log.info("CC34B9D79FC2: success to acquire lock for path:{}", keyPath);
                break;
            }catch (Exception e){
                log.info("CC34B9D79FC2: failed to acquire lock for path:{}, while try again .......", keyPath);
                try {
                    if (countDownLatch.getCount() <=0){
                        countDownLatch = new CountDownLatch(1);
                    }
                    countDownLatch.await();
                } catch (InterruptedException ex) {
                    log.error("CC34B9D79FC2: acquire lock error:{}", ex);
                }
            }
        }
    }

    /**
     * 释放分布式锁
     * @param path
     * @return
     */
    public boolean releaseDistributedLock(String path){
        String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        try {
            if (curatorFramework.checkExists().forPath(keyPath) != null){
                curatorFramework.delete().forPath(keyPath);
            }
        } catch (Exception e) {
            log.error("CC34B9D79FC2: failed to release lock:{}", e);
            return false;
        }
        return true;
    }

    /**
     * 创建 watcher 事件
     */
    private void addWatcher(String path) throws Exception{
        String keyPath;
        if (path.equals(ROOT_PATH_LOCK)) {
            keyPath = "/" + path;
        } else {
            keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        }
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorFramework, keyPath, false);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener((client, event) -> {
            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                String oldPath = event.getData().getPath();
                log.info("CC34B9D79FC2: 上一个节点 "+ oldPath + " 已经被断开");
                if (oldPath.contains(path)){
                    //释放计数器，让当前的请求获取锁
                    countDownLatch.countDown();
                }

            }

        });
    }

    //对象及属性初始化完成后执行，也可使用@Bean的initMethod 初始化创建
    //创建父节点(永久节点 /lock-namespace/rootLock)
    @Override
    public void afterPropertiesSet(){
        //在create ZNode的时候都会自动加上这个namespace作为这个node path的root.
        curatorFramework = curatorFramework.usingNamespace("lock-namespace");
        String path = "/" + ROOT_PATH_LOCK;
        try {
            if (curatorFramework.checkExists().forPath(path) == null) {
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path);
            }
            addWatcher(ROOT_PATH_LOCK);
            log.info("CC34B9D79FC2: root path 的 watcher 事件创建成功");
        } catch (Exception e) {
            log.error("CC34B9D79FC2: 创建父节点或者addWatcher异常:{}", e);
        }
    }
}

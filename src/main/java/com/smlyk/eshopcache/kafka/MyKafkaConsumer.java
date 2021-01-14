package com.smlyk.eshopcache.kafka;

import com.alibaba.fastjson.JSON;
import com.smlyk.eshopcache.dto.ProductInfo;
import com.smlyk.eshopcache.dto.UpdateMsgDto;
import com.smlyk.eshopcache.rebuild.RebuildCacheQueue;
import com.smlyk.eshopcache.service.ISaveToCacheService;
import com.smlyk.eshopcache.zookeeper.ZkDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @Author: always
 * @Date: 2021/1/5 5:24 下午
 */
@Component
@Slf4j
public class MyKafkaConsumer {

    @Autowired
    private ISaveToCacheService saveToCacheService;

    @Autowired
    private ZkDistributedLock zkDistributedLock;

    @KafkaListener(topics = MyKafkaProducer.TOPIC_TEST, groupId = MyKafkaProducer.TOPIC_GROUP1)
    public void topic_test(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            log.info("topic_test 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = MyKafkaProducer.TOPIC_TEST, groupId = MyKafkaProducer.TOPIC_GROUP2)
    public void topic_test1(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            log.info("topic_test1 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = MyKafkaProducer.UPDATE_MSG_TOPIC, groupId = MyKafkaProducer.TOPIC_GROUP1)
    public void updateMsgTopic(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try {
            Optional message = Optional.ofNullable(record.value());
            if (message.isPresent()) {
                Object msg = message.get();
                log.info("updateMsgTopic消费了： Topic:" + topic + ",Message:" + msg);
                //解析收到的消息，根据type去获取对应的修改信息保存到本地缓存和Redis缓存中
                UpdateMsgDto updateMsgDto = JSON.parseObject((String) msg, UpdateMsgDto.class);
                if ("productInfo_update".equals(updateMsgDto.getType())){
                    //模拟获取到的数据
                    ProductInfo productInfo = new ProductInfo(1010, "Iphone12", "玫瑰金", 6999.00, "aa.jpg,bb.jpg",
                            "2000g", "全年保修", "5.7寸" ,20, "2021-01-12 17:02:00");

                    String path = null;
                    try {
                        //现获取分布式锁
                        path = "productInfo_lock_" + productInfo.getId();
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

//                    //保存到本地缓存
//                    saveToCacheService.saveToLocalCache(productInfo);
//
//                    //测试是否保存成功
//                    log.info("获取本地缓存数据为：{}", JSON.toJSONString(saveToCacheService.getFromLocalCache(productInfo.getId())));
//
//                    //保存到redis缓存
//                    saveToCacheService.saveToRedisCache(productInfo);
//
//                    //测试获取redis缓存数据
//                    String key = "productInfo_" + productInfo.getId();
//                    log.info("获取Redis缓存数据为：{}", JSON.toJSONString(saveToCacheService.getFromRedisCache(key)));
                }
                ack.acknowledge();
            }
        } catch (Exception e) {
            log.error("消费异常：{}", e);
            //TODO 消费异常的数据记录下来，以后处理用
            ack.acknowledge();
        }


    }




}

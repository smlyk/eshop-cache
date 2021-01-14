package com.smlyk.eshopcache.controller;

import com.smlyk.eshopcache.dto.UpdateMsgDto;
import com.smlyk.eshopcache.kafka.MyKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: always
 * @Date: 2021/1/5 5:34 下午
 */
@RestController
@RequestMapping("kafka")
public class KafkaController {

    @Autowired
    private MyKafkaProducer kafkaProducer;

    @GetMapping("send")
    public void sendMsg(){
        kafkaProducer.send("hdajkdjaldjal;dka");
    }

    @GetMapping("sendMsg")
    public void sendUpdateMsg(){
        kafkaProducer.sendUpdateMsg(new UpdateMsgDto("productInfo_update", "修改商品信息消息"));
    }
}

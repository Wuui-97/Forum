package com.wuui.community.event;

import com.alibaba.fastjson.JSONObject;
import com.wuui.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Dee
 * @create 2022-05-25-22:48
 * @describe 事件生产者，触发事件时主动调用
 */
@Component
public class EventProducer {

    @Autowired
    KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event){
        //将事件发布到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}

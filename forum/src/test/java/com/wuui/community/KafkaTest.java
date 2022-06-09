package com.wuui.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Dee
 * @create 2022-05-25-20:32
 * @describe
 */
@SpringBootTest
public class KafkaTest {

    @Autowired
    KafkaProducer kafkaProducer;

    @Test
    public void kafkaTest(){
        kafkaProducer.sendMessage("test","你好哇");
        kafkaProducer.sendMessage("test","林诗源");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
@Component
class KafkaProducer{

    @Autowired
    KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content){
        kafkaTemplate.send(topic, content);
    }

}

@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test"})
    public void reciveMessage(ConsumerRecord record){
        System.out.println(record.value());
    }

}

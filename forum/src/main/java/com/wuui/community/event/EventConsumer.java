package com.wuui.community.event;

import com.alibaba.fastjson.JSONObject;
import com.wuui.community.dao.elaticsearch.DiscussPostRepository;
import com.wuui.community.entity.Event;
import com.wuui.community.entity.Message;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.service.ElasticsearchService;
import com.wuui.community.service.MessageService;
import com.wuui.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-25-22:53
 * @describe 监听话题，队列中有消息，则取来存放到数据库中
 */
@Component
@Slf4j
public class EventConsumer implements CommunityConstant {

    @Autowired
    MessageService messageService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    DiscussPostService discussPostService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_FOLLOW, TOPIC_LIKE})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空");
            return ;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息转换格式错误");
            return ;
        }

        //创建一条通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus(0);
        message.setCreateTime(new Date());

        //通知的内容要包括当前用户和帖子信息
        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if(!event.getData().isEmpty()){
            for(Map.Entry<String, Object> entry : event.getData().entrySet()){
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        //向数据库中插入一条通知
        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空");
            return ;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息转换格式错误");
            return ;
        }

         elasticsearchService.saveDiscussPost(discussPostService.findDiscussPostById(event.getEntityId()));
    }

    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            log.error("消息的内容为空");
            return ;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            log.error("消息转换格式错误");
            return ;
        }

        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }
}

package com.wuui.community.controller;

import com.wuui.community.entity.Event;
import com.wuui.community.entity.User;
import com.wuui.community.event.EventProducer;
import com.wuui.community.service.LikeService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.HostHandler;
import com.wuui.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-22-13:30
 * @describe
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHandler hostHandler;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHandler.getUser();

        if(user == null){
            return CommunityUtil.getJSONString(403, "您还没有登录哦");
        }
        //点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        Map<String, Object> map = new HashMap<>();
        //点赞数量
        map.put("likeCount", likeService.findEntityLikeCount(entityType, entityId));
        //点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        map.put("likeStatus", likeStatus);

        //触发点赞话题事件
        if(likeStatus == 1){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setUserId(user.getId())
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        if(entityType == ENTITY_TYPE_POST){
            //需要更新的帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, entityId);
        }

        return CommunityUtil.getJSONString(200, null, map);
    }

}

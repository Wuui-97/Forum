package com.wuui.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-25-22:27
 * @describe 事件类，如评论、点赞、关注事件
 */
public class Event {

    private String topic;
    //触发事件的用户id
    private int userId;
    private int entityType;
    private int entityId;
    //实体对应的用户id
    private int entityUserId;
    //存放多余的数据
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    //返回Event，这样可以直接在后面进行拼接
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}

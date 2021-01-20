package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

    //事件主题
    private String topic;
    //事件触发的人
    private int userId;
    //操作类型   哪个实体之上
    private int entityType;
    private int entityId;
    //实体作者
    private int entityUserId;
    //额外数据  具有一定扩展性
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    //方便  连续set
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
        return data;
    }

    public Event setData(String key,String value) {
        this.data.put(key,value);
        return this;
    }
}

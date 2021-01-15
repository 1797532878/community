package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {

    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    //0未读 1已读  考虑到一方删除另一方不删除的话要重新加字段
    private int status;
    private Date createTime;

}

package com.wuui.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author Dee
 * @create 2022-05-19-18:44
 * @describe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

    @TableId(type = IdType.AUTO)
    private Integer id;
    //1：系统id，其他：用户id
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    //0: 未读，1: 已读， 2：删除
    private int status;
    private Date createTime;

}

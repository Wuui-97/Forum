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
 * @create 2022-05-17-18:18
 * @describe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private int userId;
    //帖子：1； 评论：2
    private int entityType;
    //评论帖子的id
    private int entityId;
    //回复的目标，为 0 则表示没有目标，其他值则需要去找对应用户
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

}

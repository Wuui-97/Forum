package com.wuui.community.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author
 * @create 2022-05-08-22:30
 * @discribe 社区首页
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DiscussPost {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private Date createTime;
    private Integer commentCount;
    private Integer score;
}

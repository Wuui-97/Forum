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
 * @create 2022-05-08-17:08
 * @discribe 用户信息
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    //0：普通用户；1：管理员；2：超级管理员
    private int type;
    //0：未激活；1：已激活
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}

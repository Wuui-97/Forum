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
 * @create 2022-05-12-21:16
 * @describe
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginTicket {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    //存放在cookie中，作为登录凭证
    private String ticket;
    //0：有效，1：无效
    private Integer status;
    private Date expired;
}

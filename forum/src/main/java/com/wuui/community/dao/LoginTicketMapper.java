package com.wuui.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuui.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Update;

/**
 * @author Dee
 * @create 2022-05-12-21:30
 * @describe
 */
@Deprecated
public interface LoginTicketMapper extends BaseMapper<LoginTicket> {

    @Update("update login_ticket set status = #{status} where ticket = #{ticket}")
    void updateByTicket(int status, String ticket);

}

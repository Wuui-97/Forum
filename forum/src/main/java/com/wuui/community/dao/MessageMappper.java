package com.wuui.community.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuui.community.entity.Message;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-19-18:52
 * @describe
 */

public interface MessageMappper extends BaseMapper<Message> {

    //更改状态
    int updateStatus(List<Integer> ids, int status);
    //查询某一个话题下的最新通知
    Message selectLatestNotice(int userId, String topic);
    //某一个话题下的通知数量
    int selectNoticeCount(int userId, String topic);
    //某一话题下的未读通知数量
    int selectNoticeUnreadCount(int userId, String topic);
}

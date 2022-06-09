package com.wuui.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuui.community.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-19-18:53
 * @describe
 */
@Service
public interface MessageService extends IService<Message> {

    //获取会话列表
    Page<Message> findConversations(int userId, int pn, int limit);

    int findConversaionCount(int userId);

    //获取一条会话中的消息列表
    Page<Message> findLetters(String conversationId, int pn, int limit);

    int findLetterCount(String conversationId);

    //获取一条会话未读消息数量
    int findLetterUnreadCount(int userId, String conversationId);

    //插入一条私信
    int addMessage(Message message);

    //消息已读，更改其status
    int readMessage(List<Integer> ids);

    //查询topic最近的通知
    Message findLatestNotice(int userId, String topic);

    //查询topic的通知数量
    int findNoticeCount(int userId, String topic);

    //查询未读的通知数量
    int findNoticeUnreadCount(int userId, String topic);

    //查询某一话题的通知数
    Page<Message> findNotices(int userId, String topic, int pn, int limit);

}

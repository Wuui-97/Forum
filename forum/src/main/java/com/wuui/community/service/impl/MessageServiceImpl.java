package com.wuui.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuui.community.entity.Message;
import com.wuui.community.dao.MessageMappper;
import com.wuui.community.service.MessageService;
import com.wuui.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-19-18:53
 * @describe
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMappper, Message> implements MessageService {

    @Autowired
    MessageMappper messageMappper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Override
    public Page<Message> findConversations(int userId, int pn, int limit) {

        Page<Message> page = new Page<>(pn, limit);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("id","SELECT MAX(id) FROM message\n" +
                                                "WHERE `status` != 2\n" +
                                                "AND from_id != 1\n" +
                                                "AND (from_id = " + userId +" OR to_id = " + userId +")\n" +
                                                "GROUP BY conversation_id\n")
                    .orderByDesc("id");
        Page<Message> messagePage = messageMappper.selectPage(page, queryWrapper);

        return messagePage;
    }

    @Override
    public int findConversaionCount(int userId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("id","SELECT MAX(id) FROM message\n" +
                                                "WHERE `status` != 2\n" +
                                                "AND from_id != 1\n" +
                                                "AND (from_id = " + userId +" OR to_id = " + userId +")\n" +
                                                "GROUP BY conversation_id\n");
        return messageMappper.selectCount(queryWrapper);
    }

    @Override
    public Page<Message> findLetters(String conversationId, int pn, int limit) {

        Page<Message> page = new Page<>(pn, limit);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", conversationId)
                    .ne("status",2)
                    .ne("from_id",1)
                    .orderByDesc("id");
        Page<Message> messagePage = messageMappper.selectPage(page, queryWrapper);

        return messagePage;
    }

    @Override
    public int findLetterCount(String conversationId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", conversationId)
                .ne("status",2)
                .ne("from_id",1);
        return messageMappper.selectCount(queryWrapper);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id",userId)
                    .eq("status",0)
                    .ne("from_id",1)
                    .eq(conversationId != null, "conversation_id",conversationId);
        return messageMappper.selectCount(queryWrapper);
    }

    @Override
    public int addMessage(Message message) {

        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));

        return messageMappper.insert(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMappper.updateStatus(ids, 1);
    }

    //查询topic最近的通知
    public Message findLatestNotice(int userId, String topic){
//        return messageMappper.selectLatestNotice(userId, topic);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("id", "SELECT MAX(id)\n" +
                                                "\tFROM message\n" +
                                                "\tWHERE 'status' != 2\n" +
                                                "\tAND from_id = 1\n" +
                                                "\tAND to_id = " + userId +
                                                "\tAND conversation_id = " + "'" +topic + "'");
        return messageMappper.selectOne(queryWrapper);
    }

    //查询topic的通知数量
    public int findNoticeCount(int userId, String topic){
        return messageMappper.selectNoticeCount(userId, topic);
    }

    //查询未读的通知数量
    public int findNoticeUnreadCount(int userId, String topic){
        return messageMappper.selectNoticeUnreadCount(userId, topic);
    }

    //查询某一话题的通知数
    public Page<Message> findNotices(int userId, String topic, int pn, int limit){

        Page<Message> page = new Page<>(pn, limit);
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("to_id", userId)
                .ne("status", 2)
                .eq("conversation_id", topic)
                .orderByDesc("create_time");

        return messageMappper.selectPage(page, queryWrapper);
    }
}

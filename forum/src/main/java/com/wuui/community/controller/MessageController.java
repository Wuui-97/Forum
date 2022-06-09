package com.wuui.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuui.community.entity.Message;
import com.wuui.community.entity.User;
import com.wuui.community.service.MessageService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author Dee
 * @create 2022-05-19-21:36
 * @describe
 */
@Controller
public class MessageController implements CommunityConstant {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHandler hostHandler;

    /**
     * 私信列表
     * @param model
     * @param pn
     * @return
     */
    @GetMapping("/letter/list")
    public String listLetter(Model model,
                             @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn){
        User user = hostHandler.getUser();
        Page<Message> messagePage = messageService.findConversations(user.getId(), pn, 10);
        List<Message> messages = messagePage.getRecords();
        System.out.println("总页码为：" + messagePage.getPages());
        System.out.println("总记录为：" + messagePage.getTotal());

        List<Map<String,Object>> conversations = new ArrayList<>();
        for (Message message : messages) {
            Map<String,Object> map = new HashMap<>();
            map.put("message",message);
            map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
            map.put("unReadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));

            int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
            User target = userService.findUserById(targetId);
            map.put("target",target);

            conversations.add(map);

        }
        model.addAttribute("conversations",conversations);
        //私信未读数量
        int letterUnReadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnReadCount", letterUnReadCount);
        //系统通知未读数量
        int noticeUnReadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnReadCount", noticeUnReadCount);

        //page的相关信息
        model.addAttribute("page", messagePage);
//        model.addAttribute("pageDisplayNum", PAGE_DISPLAY_NUM);
        model.addAttribute("pagePath", "/letter/list");

        return "site/letter";
    }

    /**
     * 私信详情
     * @param model
     * @param conversationId
     * @param pn
     * @return
     */
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(Model model,
                                  @PathVariable("conversationId") String conversationId,
                                  @RequestParam(value = "pn", required = false, defaultValue = "1") Integer pn){

        Page<Message> letterPage = messageService.findLetters(conversationId, pn, 10);
        List<Message> records = letterPage.getRecords();

        //存放每一条message相应信息的list
        List<Map<String, Object>> letters = new ArrayList<>();
        //未读消息的id集合
        List<Integer> ids = new ArrayList<>();
        for (Message message : records) {
            //每一条消息信息存放进map
            Map<String, Object> map = new HashMap<>();
            map.put("message", message);
            map.put("fromUser", userService.findUserById(message.getFromId()));
            letters.add(map);
//            System.out.println(hostHandler.getUser().getId().equals(message.getToId()));
//            System.out.println(message.getStatus() == 0);

            //获取当前用户是接收者时，未读消息的id
            if(hostHandler.getUser().getId().equals(message.getToId()) && message.getStatus() == 0){
                ids.add(message.getId());
            }

        }
        //将未读消息更新为已读
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        model.addAttribute("letters", letters);
        model.addAttribute("target", getUserByConversationId(conversationId));

        model.addAttribute("page", letterPage);
//        model.addAttribute("pageDisplayNum", PAGE_DISPLAY_NUM);
        model.addAttribute("pagePath", "/letter/detail/" + conversationId);

        return "site/letter-detail";

    }

    private User getUserByConversationId(String conversationId){
        String[] strings = conversationId.split("_");
        int i_0 = Integer.parseInt(strings[0]);
        int i_1 = Integer.parseInt(strings[1]);

        if(hostHandler.getUser().getId() == i_0){
            return userService.findUserById(i_1);
        }else{
            return userService.findUserById(i_0);
        }
    }

    /**
     * 发送私信
     * @param toName
     * @param content
     * @return
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){

        User target = userService.getOne(new QueryWrapper<User>().eq("username", toName));
        if(target == null){
            return CommunityUtil.getJSONString(400, "目标用户不存在");
        }

        User user = hostHandler.getUser();

        String conversationId = getConversationId(user, target);
        Message message = new Message(null, user.getId(), target.getId(), conversationId, content, 0, new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(200, "发送成功");
    }

    private String getConversationId(User user, User target){
        if(user.getId() < target.getId()){
            return user.getId() + "_" + target.getId();
        }else {
            return target.getId() + "_" + user.getId();
        }
    }

    /**
     * 通知列表
     * @param model
     * @return
     */
    @GetMapping("/notice/list")
    public String getNoticeListPage(Model model){
        User user = hostHandler.getUser();
        if(user == null){
            throw new IllegalArgumentException("用户参数不能为空");
        }

        //查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);

        //存放要返回的数据
        Map<String, Object> messageVo = new HashMap<>();
        messageVo.put("message",message);
        if(message != null){
            //将转义字符再转义
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap map = JSONObject.parseObject(content, HashMap.class);
            if(!map.isEmpty()){
                messageVo.put("entityType", map.get("entityType"));
                messageVo.put("entityId", map.get("entityId"));
                messageVo.put("postId", map.get("postId"));
                User fromUser = userService.findUserById((int) map.get("userId"));
                messageVo.put("user", fromUser);
            }
            //评论通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);
            //评论通知未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unread", unread);

        }
        model.addAttribute("commentNotice", messageVo);

        //查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        //存放要返回的数据
        messageVo = new HashMap<>();
        messageVo.put("message",message);

        if(message != null){
            //将转义字符再转义
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap map = JSONObject.parseObject(content, HashMap.class);
            if(!map.isEmpty()){
                messageVo.put("entityType", map.get("entityType"));
                messageVo.put("entityId", map.get("entityId"));
                messageVo.put("postId", map.get("postId"));
                User fromUser = userService.findUserById((int) map.get("userId"));
                messageVo.put("user", fromUser);
            }
            //点赞通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);
            //点赞通知未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unread", unread);

        }
        model.addAttribute("likeNotice", messageVo);

        //查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        //存放要返回的数据
        messageVo = new HashMap<>();
        messageVo.put("message",message);

        if(message != null){
            //将转义字符再转义
            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap map = JSONObject.parseObject(content, HashMap.class);
            if(!map.isEmpty()){
                messageVo.put("entityType", map.get("entityType"));
                messageVo.put("entityId", map.get("entityId"));
                User fromUser = userService.findUserById((int) map.get("userId"));
                messageVo.put("user", fromUser);
            }
            //关注通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);
            //关注通知未读数量
            int unread = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unread", unread);

        }
        model.addAttribute("followNotice", messageVo);

        //私信未读数量
        int letterUnReadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnReadCount", letterUnReadCount);
        //系统通知未读数量
        int noticeUnReadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnReadCount", noticeUnReadCount);

        return "site/notice";
    }

    /**
     * 通知详情
     * @param topic
     * @param pn
     * @param model
     * @return
     */
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetailPage(@PathVariable("topic") String topic,
                                      @RequestParam(name = "pn", required = false, defaultValue = "1") Integer pn,
                                      Model model){
        User user = hostHandler.getUser();
        if(user == null){
            throw new IllegalArgumentException("用户参数不能为空");
        }

        Page<Message> noticesPage = messageService.findNotices(user.getId(), topic, pn, 5);
        //获取到当前页的通知
        List<Message> notices = noticesPage.getRecords();

        //存放返回的通知信息
        List<Map<String, Object>> noticesList = new ArrayList<>();
        //存放未读通知集合
        List<Integer> ids = new ArrayList<>();
        for (Message message : notices) {
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            HashMap data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));
            messageVo.put("pn", data.get("pn"));
            //哪一个用户触发了该通知
            User fromUser = userService.findUserById((int) data.get("userId"));
            messageVo.put("fromUser", fromUser);
            //系统User
            User noticeUser = userService.findUserById(message.getFromId());
            messageVo.put("noticeUser", noticeUser);

            noticesList.add(messageVo);

            //获取当前用户是接收者时，未读消息的id
            if(hostHandler.getUser().getId().equals(message.getToId()) && message.getStatus() == 0){
                ids.add(message.getId());
            }
        }
        //将未读通知更新为已读
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        model.addAttribute("notices", noticesList);
        //记录Page信息
        model.addAttribute("page", noticesPage);
        model.addAttribute("pagePath", "/notice/detail/" + topic);

        return "site/notice-detail";
    }
}

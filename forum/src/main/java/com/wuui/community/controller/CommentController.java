package com.wuui.community.controller;

import com.wuui.community.entity.Comment;
import com.wuui.community.entity.DiscussPost;
import com.wuui.community.entity.Event;
import com.wuui.community.event.EventProducer;
import com.wuui.community.service.CommentService;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.HostHandler;
import com.wuui.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;

/**
 * @author Dee
 * @create 2022-05-18-20:44
 * @describe
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    HostHandler hostHandler;

    @Autowired
    CommentService commentService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;

    @PostMapping("/add/{discussPostId}/{pn}")
    public String addComment(@PathVariable("discussPostId") Integer discussPostId,
                             @PathVariable("pn") Integer pn,
                             Comment comment,
                             RedirectAttributes redirectAttributes){

        comment.setUserId(hostHandler.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHandler.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discussPostId)
                .setData("pn", pn);
        //评论的是帖子
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
            event.setEntityUserId(discussPost.getUserId());
        //评论的是回复
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        //发布话题
        eventProducer.fireEvent(event);

        if(comment.getEntityType() == ENTITY_TYPE_POST){
            //触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            //需要更新的帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        redirectAttributes.addAttribute("pn",pn);
        return "redirect:/discuss/detail/" + discussPostId;
    }

}

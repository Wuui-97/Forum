package com.wuui.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuui.community.annotation.LoginRequired;
import com.wuui.community.entity.Comment;
import com.wuui.community.entity.DiscussPost;
import com.wuui.community.entity.Event;
import com.wuui.community.entity.User;
import com.wuui.community.event.EventProducer;
import com.wuui.community.service.CommentService;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.service.LikeService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.HostHandler;
import com.wuui.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Dee
 * @create 2022-05-15-19:47
 * @describe 关于帖子的相关操作
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    UserService userService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHandler hostHandler;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 发布帖子
     * @param title
     * @param content
     * @return
     */
    @LoginRequired
    @PostMapping("/add")
    @ResponseBody
    public String addDiscuss(String title, String content){
        User user = hostHandler.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"您还没有登录哦！");
        }
        if(StringUtils.isBlank(title)){
            return CommunityUtil.getJSONString(400,"标题不能为空");
        }

        DiscussPost post = new DiscussPost(null, user.getId(), title, content, 0, 0, new Date(), 0, 0);
        discussPostService.addDiscussPost(post);

        //触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);

        //需要更新的帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, post.getId());

        return CommunityUtil.getJSONString(200,"发布成功");
    }

    /**
     * 显示帖子详情
     * @param discussPostId
     * @param model
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String displayDiscuss(@PathVariable("discussPostId") Integer discussPostId,
                                 @RequestParam(value = "pn",required = false, defaultValue = "1") Integer pn,
                                 Model model){
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        User user = userService.findUserById(post.getUserId());
        //帖子
        model.addAttribute("post",post);
        //帖子用户
        model.addAttribute("user",user);

        //获取赞数量
        model.addAttribute("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId));
        //获取点赞状态
        model.addAttribute("likeStatus", likeService.findEntityLikeStatus(user.getId(), ENTITY_TYPE_POST, discussPostId));

        //获取到帖子的评论信息
        IPage<Comment> commentIPage = commentService.findCommentPageByEntity(ENTITY_TYPE_POST, discussPostId, pn, 5);

        //帖子评论
        List<Comment> commentRecords = commentIPage.getRecords();
        //用来返回的包装有帖子和用户信息的集合
        List<Map<String,Object>> comments = new ArrayList<>();
        for (Comment comment : commentRecords) {
            Map<String, Object> commentMap = new HashMap<>();
            //每一条帖子评论
            commentMap.put("comment",comment);
            //评论对应的user
            commentMap.put("user",userService.findUserById(comment.getUserId()));
            //评论的点赞数量
            commentMap.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));
            //评论的点赞状态
            commentMap.put("likeStatus", likeService.findEntityLikeStatus(hostHandler.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId()));

            //评论的回复信息
            IPage<Comment> replyIPage = commentService.findCommentPageByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 1, Integer.MAX_VALUE);
            //评论的回复
            List<Comment> replyRecords = replyIPage.getRecords();
            //回复的总数
            long replyTotal = replyIPage.getTotal();
            System.out.println(replyTotal);
            //用来存放每一条回复信息的list
            List<Map<String,Object>> replys = new ArrayList<>();
            for (Comment reply : replyRecords) {
                HashMap<String, Object> replyMap = new HashMap<>();
                //回复信息
                replyMap.put("comment", reply);
                //回复对应的user
                replyMap.put("user", userService.findUserById(reply.getUserId()));
                //回复的点赞数量
                replyMap.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));
                //回复的点赞状态
                replyMap.put("likeStatus", likeService.findEntityLikeStatus(hostHandler.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId()));

                //回复是否有目标
                User target = userService.findUserById(reply.getTargetId());
                replyMap.put("target", target);
                replys.add(replyMap);
            }
            commentMap.put("replyTotal",replyTotal);
            commentMap.put("replys",replys);
            comments.add(commentMap);
        }
        model.addAttribute("comments",comments);
        model.addAttribute("page",commentIPage);
        model.addAttribute("pagePath", "/discuss/detail/" + discussPostId);
//        model.addAttribute("pageDisplayNum", PAGE_DISPLAY_NUM);
//        long size = commentIPage.getSize();

        return "site/discuss-detail";
    }

    /**
     * 置顶
     * @param postId
     * @return
     */
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int postId){
        discussPostService.updateType(postId, 1);

        //触发更新帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHandler.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(200);
    }

    /**
     * 加精
     * @param postId
     * @return
     */
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int postId){
        discussPostService.updateStatus(postId, 1);

        //触发更新帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHandler.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);

        //需要更新的帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey, postId);

        return CommunityUtil.getJSONString(200);
    }

    /**
     * 删帖
     * @param postId
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int postId){
        discussPostService.updateStatus(postId, 2);

        //触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHandler.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(postId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(200);
    }

}

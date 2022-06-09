package com.wuui.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuui.community.annotation.LoginRequired;
import com.wuui.community.entity.Event;
import com.wuui.community.entity.User;
import com.wuui.community.event.EventProducer;
import com.wuui.community.service.FollowService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-22-19:02
 * @describe
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHandler hostHandler;

    @Autowired
    EventProducer eventProducer;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @LoginRequired
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHandler.getUser();
        followService.follow(user.getId(), entityType, entityId);

        //触发关注话题事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);


        return CommunityUtil.getJSONString(200);
    }

    /**
     * 取消关注
     * @param entityType
     * @param entityId
     * @return
     */
    @LoginRequired
    @PostMapping("/unFollow")
    @ResponseBody
    public String unFollow(int entityType, int entityId){
        User user = hostHandler.getUser();
        followService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(200);
    }

    /**
     * 查看关注的人
     * @param userId
     * @param pn
     * @param model
     * @return
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") Integer userId,
                               @RequestParam(name = "pn", required = false, defaultValue = "1") Integer pn,
                               Model model){
        //获取当前用户
        User user = userService.findUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("用户参数不能为空");
        }
        model.addAttribute("user", user);

        //定义Page的当前页，每页数量，总记录数
        Page page = new Page(pn, 5, followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        //关注的人的信息
        //page.offset()是查询的起始下标，page.getSize()是每一页显示的size
        List<Map<String, Object>> followees = followService.findFollowees(userId, page.offset(), page.getSize());
        model.addAttribute("followees",followees);

        //每一个关注的人的关注状态，false表明未关注
        for (Map<String, Object> map : followees) {
            User u = (User) map.get("user");
            boolean followStatus = getFollowStatus(u.getId());
            map.put("followStatus", followStatus);
        }
        //分页信息
        model.addAttribute("page", page);
        model.addAttribute("pagePath", "/followees/" + userId);

        return "site/followee";
    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param pn
     * @param model
     * @return
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") Integer userId,
                               @RequestParam(name = "pn", required = false, defaultValue = "1") Integer pn,
                               Model model){
        //获取当前用户
        User user = userService.findUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("用户参数不能为空");
        }
        model.addAttribute("user", user);

        //定义Page的当前页，每页数量，总记录数
        Page page = new Page(pn, 5, followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        //关注的人的信息
        List<Map<String, Object>> followers = followService.findFollowers(userId, page.offset(), page.getSize());
        model.addAttribute("followers",followers);

        //每一个关注的人的关注状态，false表明未关注
        for (Map<String, Object> map : followers) {
            User u = (User) map.get("user");
            boolean followStatus = getFollowStatus(u.getId());
            map.put("followStatus", followStatus);
        }
        //分页信息
        model.addAttribute("page", page);
        model.addAttribute("pagePath", "/followers/" + userId);

        return "site/follower";
    }

    private boolean getFollowStatus(int userId){
        if(hostHandler.getUser() == null){
            return false;
        }
        return followService.findFollowStatus(hostHandler.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

}

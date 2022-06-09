package com.wuui.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuui.community.entity.DiscussPost;
import com.wuui.community.entity.User;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.service.LikeService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-09-13:04
 * @describe
 */
@Controller

public class HomeController implements CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @GetMapping("/")
    public String forwardToindex(){
        return "forward:/index";
    }

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(value = "pn",required = false, defaultValue = "1") Integer pn,
                        @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){

//        long start = System.currentTimeMillis();
        Page<DiscussPost> discussPostPage = discussPostService.findDiscussPosts(0, pn, 10, orderMode);
//        long end = System.currentTimeMillis();
//        System.out.println("访问数据库所花费的时间为：" + (end - start));

        List<DiscussPost> records = discussPostPage.getRecords();
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        for (DiscussPost record : records) {
            HashMap<String, Object> postMap = new HashMap<>();
            Integer userId = record.getUserId();
            User user = userService.findUserById(userId);
            //帖子信息
            postMap.put("post",record);
            //帖子作者
            postMap.put("user",user);
            //帖子点赞数量
            postMap.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, record.getId()));

            discussPosts.add(postMap);
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode", orderMode);
        model.addAttribute("page",discussPostPage);
//        model.addAttribute("pageDisplayNum", PAGE_DISPLAY_NUM);
        model.addAttribute("pagePath", "/index?orderMode=" + orderMode);

        return "index";
    }

    @GetMapping("/error")
    public String error(){
        return "error/500";
    }

    //拒绝访问时显示的页面
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "/error/404";
    }

    @GetMapping("/test")
    public String getTestPage(){
        return "test";
    }

}

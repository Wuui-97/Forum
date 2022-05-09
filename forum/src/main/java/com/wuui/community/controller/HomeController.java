package com.wuui.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuui.community.bean.DiscussPost;
import com.wuui.community.bean.User;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.service.UserService;
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

public class HomeController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam(value = "pn",defaultValue = "1") Integer pn){

        //页码为pn，大小为5
        Page<DiscussPost> page = new Page<>(pn, 5);
        QueryWrapper<DiscussPost> wrapper = new QueryWrapper<>();
        //条件查询，user_id = 103
        wrapper.eq("user_id",103);
        IPage<DiscussPost> discussPostPage = discussPostService.page(page,null);
//        System.out.println("===========================================================");
//        System.out.println(discussPostPage.getSize());

        List<DiscussPost> records = discussPostPage.getRecords();
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        for (DiscussPost record : records) {
            HashMap<String, Object> postMap = new HashMap<>();
            Integer userId = record.getUserId();
            User user = userService.getById(userId);
            postMap.put("post",record);
            postMap.put("user",user);
            discussPosts.add(postMap);
        }
        model.addAttribute("discussPostPage",discussPostPage);
        model.addAttribute("discussPosts",discussPosts);

        return "index";
    }

}

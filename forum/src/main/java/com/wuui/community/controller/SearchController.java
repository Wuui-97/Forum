package com.wuui.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuui.community.entity.DiscussPost;
import com.wuui.community.service.ElasticsearchService;
import com.wuui.community.service.LikeService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-29-19:10
 * @describe
 */
@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    /**
     * 搜索帖子
     * @param keyword 需要传入的搜索关键字
     * @param pn
     * @param model
     * @return
     * @throws IOException
     */
    @GetMapping("/search")
    public String getSearchPage(@RequestParam("keyword") String keyword,
                                @RequestParam(name = "pn", required = false, defaultValue = "1") Integer pn,
                                Model model) throws IOException {

        Map<String, Object> postMap = elasticsearchService.searchDiscussPost(keyword, pn - 1, 10);
        List<DiscussPost> postList = (List<DiscussPost>) postMap.get("postList");
        long total = (long) postMap.get("total");

        //要返回的显示的聚合信息
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        for (DiscussPost post : postList) {
            HashMap<String, Object> map = new HashMap<>();
            //帖子
            map.put("post", post);
            //作者
            map.put("user", userService.findUserById(post.getUserId()));
            //点赞数量
            map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        //分页信息
        Page<DiscussPost> page = new Page<>(pn, 10, total);
        model.addAttribute("page", page);
        model.addAttribute("pagePath", "/search?keyword=" + keyword);

        return "site/search";
    }

}

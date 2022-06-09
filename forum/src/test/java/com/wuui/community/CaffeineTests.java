package com.wuui.community;

import com.wuui.community.entity.DiscussPost;
import com.wuui.community.service.DiscussPostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author Dee
 * @create 2022-06-04-22:07
 * @describe
 */
@SpringBootTest
public class CaffeineTests {

    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("君不见，黄河之水天上来，奔流到海不复回");
            post.setContent("君不见，高堂明镜悲白发，朝如青丝暮成雪！" +
                    "人生得意须尽欢，莫使金樽空对月！" +
                    "天生我材必有用，千金散尽还复来！" +
                    "烹羊宰牛且为乐，会须一饮三百杯");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            postService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache(){
        System.out.println(postService.findDiscussPosts(0, 1, 10, 1).getRecords());
        System.out.println(postService.findDiscussPosts(0, 1, 10, 1).getRecords());
        System.out.println(postService.findDiscussPosts(0, 1, 10, 1).getRecords());
        System.out.println(postService.findDiscussPosts(0, 1, 10, 0).getRecords());
    }

}

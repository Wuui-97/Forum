package com.wuui.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuui.community.bean.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-09-12:44
 * @describe
 */
@Service
public interface DiscussPostService extends IService<DiscussPost> {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);
    int findDiscussPostRow(int userId);
}

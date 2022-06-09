package com.wuui.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuui.community.entity.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-09-12:44
 * @describe
 */
@Service
public interface DiscussPostService extends IService<DiscussPost> {

    Page<DiscussPost> findDiscussPosts(int userId, int pn, int limit, int orderMode);

    int findDiscussPostRow(int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDiscussPostById(int id);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}

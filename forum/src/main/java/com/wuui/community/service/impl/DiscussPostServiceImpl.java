package com.wuui.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuui.community.bean.DiscussPost;
import com.wuui.community.mapper.DiscussPostMapper;
import com.wuui.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-09-12:48
 * @describe
 */
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper,DiscussPost> implements DiscussPostService {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }

    @Override
    public int findDiscussPostRow(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}

package com.wuui.community.service;

import com.wuui.community.entity.DiscussPost;
import com.wuui.community.entity.User;
import com.wuui.community.dao.DiscussPostMapper;
import com.wuui.community.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Dee
 * @create 2022-05-16-21:58
 * @describe
 */
@Service
public class TestService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;


    /**
     * 测试事务的传播行为
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public void addDiscussPost(){
        DiscussPost discussPost = new DiscussPost(null, 12, "asdfa", "sadfasd", 0, 0, new Date(), 12, 123);
        discussPostMapper.insert(discussPost);

        addUser();

        int i = 1 / 0;
    }

    void addUser(){
        User user = new User(null, "xiari", "123456789", "23423", "2123@126.com", 0, 0, "23423jfal", "fdasfsadf", new Date());
        userMapper.insert(user);
    }

}

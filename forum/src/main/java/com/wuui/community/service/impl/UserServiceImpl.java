package com.wuui.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuui.community.bean.User;
import com.wuui.community.mapper.UserMapper;
import com.wuui.community.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Dee
 * @create 2022-05-09-12:45
 * @describe
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}

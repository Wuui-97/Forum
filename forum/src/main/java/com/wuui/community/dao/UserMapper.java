package com.wuui.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuui.community.entity.User;

/**
 * @author
 * @create 2022-05-08-17:22
 */
public interface UserMapper extends BaseMapper<User> {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String HeaderUrl);

    int updatePassword(int id, String password);
}

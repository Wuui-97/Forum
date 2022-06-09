package com.wuui.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuui.community.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-09-12:43
 * @describe
 */
@Service
public interface UserService extends IService<User> {

    Map<String,Object> register(User user, String confimPassword);

    int activation(int userId, String activationCode);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    User findUserByTicket(String ticket);

    User findUserById(int userId);

    void updateHeader(int userId, String headerUrl);

    void updatePassword(int userId, String password);

    //定义用户的权限
    Collection<? extends GrantedAuthority> getAuthorities(int userId);
}

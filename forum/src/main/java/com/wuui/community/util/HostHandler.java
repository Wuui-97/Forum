package com.wuui.community.util;

import com.wuui.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author Dee
 * @create 2022-05-13-21:52
 * @describe 为线程保存对象
 */
@Component
public class HostHandler {

    //对于ThreadLocal对象，一般定义为private static，再手动释放，防止内存泄漏
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setUser(User user){
        userThreadLocal.set(user);
    }

    public User getUser(){
         return userThreadLocal.get();
    }

    public void remove(){
        //将ThreacLocalMap中保存的entry对象消除
        userThreadLocal.remove();
    }
}

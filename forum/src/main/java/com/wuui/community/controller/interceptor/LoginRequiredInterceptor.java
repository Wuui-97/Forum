package com.wuui.community.controller.interceptor;

import com.wuui.community.annotation.LoginRequired;
import com.wuui.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Dee
 * @create 2022-05-14-18:24
 * @describe
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHandler hostHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //当前handler是否是方法
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取访问方法上是否有LoginRequired注解
            LoginRequired required = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if(required != null && hostHandler.getUser() == null){
                //重定向到首页
                response.sendRedirect(request.getContextPath() + "/index");
                return false;
            }
        }

        return true;
    }
}

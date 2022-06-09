package com.wuui.community.controller.interceptor;

import com.wuui.community.entity.User;
import com.wuui.community.service.DateService;
import com.wuui.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dee
 * @create 2022-06-01-19:09
 * @describe
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    DateService dateService;

    @Autowired
    HostHandler hostHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dateService.recordUV(ip);

        //统计DAU
        User user = hostHandler.getUser();
        if(user != null){
            dateService.recordDAU(user.getId());
        }

        return true;
    }
}

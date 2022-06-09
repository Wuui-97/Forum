package com.wuui.community.controller.interceptor;

import com.wuui.community.entity.User;
import com.wuui.community.service.MessageService;
import com.wuui.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dee
 * @create 2022-05-27-14:53
 * @describe 消息拦截器，为了显示消息数量
 */
@Component
public class MessageIntercptor implements HandlerInterceptor {

    @Autowired
    HostHandler hostHandler;

    @Autowired
    MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHandler.getUser();
        if(user != null && modelAndView != null){
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("unreadAllCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}

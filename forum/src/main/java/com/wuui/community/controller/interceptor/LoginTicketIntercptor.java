package com.wuui.community.controller.interceptor;

import com.wuui.community.entity.User;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CookieUtil;
import com.wuui.community.util.HostHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dee
 * @create 2022-05-13-21:07
 * @describe
 */
@Component
public class LoginTicketIntercptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    HostHandler hostHandler;

    /**
     * 在Controller方法执行之前，拦截所有请求，查看是否有ticket
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getCookieValue(request, "ticket");
        if(ticket != null){
            //通过Ticket获取对应的User
            User user = userService.findUserByTicket(ticket);
            if(user != null){
                //为当前线程保存用户信息，防止线程安全问题
                hostHandler.setUser(user);

                //认证成功后，将用户认证信息封装到authentication，并 存入SecurityContext，以便于Security授权
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));

            }
        }
        return true;
    }

    /**
     * 在Controller方法执行之后，模板解析渲染之前，将User对象存放到ModelAndView中
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //当前线程的ThreadLocalMap中是否存放有User对象
        User user = hostHandler.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    /**
     * 页面渲染完成之后执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //最后记得释放创建的对象
        hostHandler.remove();
        //清除保存的authentication信息
//        SecurityContextHolder.clearContext();
    }
}

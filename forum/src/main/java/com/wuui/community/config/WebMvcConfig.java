package com.wuui.community.config;

import com.wuui.community.controller.interceptor.DataInterceptor;
import com.wuui.community.controller.interceptor.LoginRequiredInterceptor;
import com.wuui.community.controller.interceptor.LoginTicketIntercptor;
import com.wuui.community.controller.interceptor.MessageIntercptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Dee
 * @create 2022-05-13-21:09
 * @describe
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginTicketIntercptor loginTicketIntercptor;

//    @Autowired
//    LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    MessageIntercptor messageIntercptor;

    @Autowired
    DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketIntercptor)
                .excludePathPatterns("/css/**","/img/**","/js/**");

//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/css/**","/img/**","/js/**");

        registry.addInterceptor(messageIntercptor)
                .excludePathPatterns("/css/**","/img/**","/js/**");

        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/css/**","/img/**","/js/**");
    }
}

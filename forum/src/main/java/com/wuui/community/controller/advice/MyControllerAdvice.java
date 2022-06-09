package com.wuui.community.controller.advice;

import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

/**
 * @author Dee
 * @create 2022-05-20-20:12
 * @describe 全局异常处理及预设全局数据
 */
@Slf4j
@ControllerAdvice(annotations = {Controller.class})
public class MyControllerAdvice implements CommunityConstant {

    /**
     * 全局异常统一处理
     * @param e
     * @param request
     * @param response
     * @throws IOException
     */
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器异常：" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(500, "服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

    /**
     * 预设全局数据
     * @param model
     */
    @ModelAttribute
    public void perseParam(Model model){
        model.addAttribute("pageDisplayNum", PAGE_DISPLAY_NUM);
    }

}

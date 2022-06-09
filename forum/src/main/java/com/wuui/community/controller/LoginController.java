package com.wuui.community.controller;

import com.google.code.kaptcha.Producer;
import com.wuui.community.entity.User;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Dee
 * @create 2022-05-10-12:32
 * @describe 登录Controller
 */
@Controller
@Slf4j
public class LoginController implements CommunityConstant {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @Autowired
    Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @GetMapping("/register")
    public String register(){
        return "site/register";
    }

    @GetMapping("/login")
    public String login(){
        return "site/login";
    }

    /**
     * 提交注册
     * @param model
     * @param user
     * @return
     */
    @PostMapping("/submitRegister")
    public String submitRegister(Model model, User user, String confimPassword){
        Map<String, Object> map = userService.register(user, confimPassword);
        if((map == null || map.isEmpty())){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快登录激活");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("confimPasswordMsg",map.get("confimPasswordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";
        }
    }

    /**
     * 提交登录
     * @return
     */
    @PostMapping("/submitLogin")
    public String submitLogin(String username, String password, String code, boolean rememberme,
                              Model model/*, HttpSession session*/, HttpServletResponse response,
                              @CookieValue(name = "kaptchaOwner", required = false) String kaptchaOwner){
//        String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if(!StringUtils.isBlank(kaptchaOwner)){
            //从Redis中获取验证码
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确");
            return "site/login";
        }

        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
            cookie.setPath(contextPath);  //要指定好哪些路径才发送cookie，提高安全性
            cookie.setMaxAge(expiredSeconds); //指定cookie的存活时间
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "site/login";
        }

    }

    /**
     * 退出
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/index";
    }

    /**
     * 激活账号
     * @param model
     * @param userId
     * @param activationCode
     * @return
     */
    //http://www.nowcoder.com/activation/id/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,
                             @PathVariable("userId") Integer userId,
                             @PathVariable("code") String activationCode){
        int status = userService.activation(userId, activationCode);
        if(status == CommunityConstant.ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号可以正常使用了");
            model.addAttribute("target","/login");
        }else if(status == CommunityConstant.ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，该账号已经激活过了");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，您提供的激活码不正确");
            model.addAttribute("target","/index");
        }
        return "site/operate-result";
    }

    /**
     * 生成验证码
     * @param response
     */
    @GetMapping("/kaptcha")
    public void kaptchaProducer(HttpServletResponse response/*, HttpSession session*/){
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
//        System.out.println(text);

        //将验证码存入Session
        // 在当前服务器路径下都有效，为了在后面输入验证码时进行比较
//        session.setAttribute("kaptcha",text);

        //将验证码存入Redis
        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //往Redis中放入验证码，同时设置过期时间
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);

        try {
            //设置将图片输出的类型
            response.setContentType("image/png");
            ServletOutputStream outputStream = response.getOutputStream();
            //将图片输出到浏览器
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            log.error("响应验证码失败" + e.getMessage());
        }
    }

}

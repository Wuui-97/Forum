package com.wuui.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuui.community.entity.LoginTicket;
import com.wuui.community.entity.User;
import com.wuui.community.dao.UserMapper;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.CommunityUtil;
import com.wuui.community.util.MailClient;
import com.wuui.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.xml.bind.annotation.XmlType;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Dee
 * @create 2022-05-09-12:45
 * @describe
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService, CommunityConstant {

    @Autowired
    UserMapper userMapper;

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

//    @Autowired
//    LoginTicketMapper loginTicketMapper;
//
//    @Autowired
//    LoginTicketService loginTicketService;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    @Override
    public Map<String, Object> register(User user, String confimPassword) {
        Map<String, Object> map = new HashMap<>();
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        if(!user.getPassword().equals(confimPassword)){
            map.put("confimPasswordMsg","两次输入密码不一致");
            return map;
        }

        if(userMapper.selectByName(user.getUsername()) != null){
            map.put("usernameMsg","用户名已被占用，请重新输入");
            return map;
        }
        if(userMapper.selectByEmail(user.getEmail()) != null){
            map.put("emailMsg","邮箱已经存在");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        //对用户输入的密码加上salt，再进行加密
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insert(user);

        //给用户发送激活邮件
        Context context = new Context();
        context.setVariable("user",user);
        //激活链接类似http://www.nowcoder.com/activation/id/code
        String activateUrl = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("activateUrl",activateUrl);

        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(),"校园网账号激活",content);

        return map;
    }

    @Override
    public int activation(int userId, String activationCode) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return CommunityConstant.ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(activationCode)){
            userMapper.updateStatus(userId,1);
            return CommunityConstant.ACTIVATION_SUCCESS;
        }else{
            return CommunityConstant.ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg","用户名不存在");
            return map;
        }
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        password = password + user.getSalt();
        if(!user.getPassword().equals(CommunityUtil.md5(password))){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //登录成功，创建凭证并保存到数据库中
        LoginTicket ticket = new LoginTicket(null, user.getId(), CommunityUtil.generateUUID(), 0, new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketService.save(ticket);
        //使用Redis来存储登录凭证信息
        String ticketKey = RedisKeyUtil.getTicketKey(ticket.getTicket());
        redisTemplate.opsForValue().set(ticketKey, ticket);

        //ticket保存要保存到客户端cookie中，以找到用户信息
        map.put("ticket",ticket.getTicket());

        return map;
    }

    public void logout(String ticket){
//        loginTicketMapper.updateByTicket(1,ticket);
        //先从Redis中获取loginTicket
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        //更改状态
        loginTicket.setStatus(1);
        //再重新放入
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    /**
     * 通过登录凭证查询到User
     * @param ticket
     * @return
     */
    public User findUserByTicket(String ticket){
//        QueryWrapper<LoginTicket> queryWrapper = new QueryWrapper<>();
////        queryWrapper.eq("ticket",ticket);
////        LoginTicket loginTicket = loginTicketService.getOne(queryWrapper);

        //通过Redis查询登录凭证ticket
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);

        //当状态有效且没过期，才返回User对象
        if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
            return userMapper.selectById(loginTicket.getUserId());
        }
        return null;
    }

    public User findUserById(int userId){
        //优先在缓存中查找
        User user = getCacheUserById(userId);
        if(user == null){
            //缓存中没有，再进行初始化
            user = initCacheUser(userId);
        }
        return user;
    }

    @Override
    public void updateHeader(int userId, String headerUrl) {
//        userMapper.updateHeader(userId,headerUrl);
        userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
    }

    public void updatePassword(int userId, String password){
        User user = getCacheUserById(userId);
        if(user == null){
            //缓存中没有，再进行初始化
            user = initCacheUser(userId);
        }

        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(userId,password);

        clearCache(userId);
    }

    /**
     * 利用Redis来缓存用户信息
     */
    //1.优先从Redis缓存中获取值
    private User getCacheUserById(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    //2.取不到时初始化Redis缓存
    private User initCacheUser(int userId){
        //缓存中没有，第一次先从数据库中查找
        User user = userMapper.selectById(userId);

        String userKey = RedisKeyUtil.getUserKey(userId);
        //初始化保存到缓存中
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);

        return user;
    }

    //3.数据变更时清除缓存
    private void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

    //定义用户的权限
    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
        User user = this.findUserById(userId);
        if(user == null){
            throw new IllegalArgumentException("用户不能为空");
        }

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()){
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }
}

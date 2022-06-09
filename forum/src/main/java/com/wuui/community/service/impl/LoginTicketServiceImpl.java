package com.wuui.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuui.community.entity.LoginTicket;
import com.wuui.community.dao.LoginTicketMapper;
import com.wuui.community.service.LoginTicketService;
import org.springframework.stereotype.Service;

/**
 * @author Dee
 * @create 2022-05-12-21:32
 * @describe
 */
@Service
public class LoginTicketServiceImpl extends ServiceImpl<LoginTicketMapper, LoginTicket> implements LoginTicketService {
}

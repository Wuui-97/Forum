package com.wuui.community.service;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Dee
 * @create 2022-05-31-23:30
 * @describe
 */
@Service
public interface DateService {

    //将指定的ip记录到UV
    public void recordUV(String ip);

    //记录区间的UV
    public long calculateUV(Date start, Date end);

    //将指定的userId记录到DAU
    public void recordDAU(int userId);

    //记录区间的DAU
    public long calculateDAU(Date start, Date end);

}

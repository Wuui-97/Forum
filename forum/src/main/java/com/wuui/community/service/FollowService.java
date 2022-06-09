package com.wuui.community.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-22-18:06
 * @describe
 */
@Service
public interface FollowService {

    //关注
    public void follow(int userId, int entityType, int entityId);

    //取消关注
    public void unFollow(int userId, int entityType, int entityId);

    //关注了多少实体
    public long findFolloweeCount(int userId, int entityType);

    //实体对应的粉丝数量
    public long findFollowerCount(int entityType, int entityId);

    //关注状态
    public boolean findFollowStatus(int userId, int entityType, int entityId);

    //关注的人的列表
    public List<Map<String, Object>> findFollowees(int userId, long offset, long limit);

    //粉丝列表
    public List<Map<String, Object>> findFollowers(int userId, long offset, long limit);

}

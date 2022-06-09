package com.wuui.community.service.impl;

import com.wuui.community.entity.User;
import com.wuui.community.service.FollowService;
import com.wuui.community.service.UserService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Dee
 * @create 2022-05-22-18:45
 * @describe
 */
@Service
public class FollowServiceImpl implements FollowService, CommunityConstant {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    /**
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    @Override
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                //开启事务
                operations.multi();
                //添加到关注的集合
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                //添加到粉丝的集合
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                //提交事务
                return operations.exec();
            }
        });
    }

    @Override
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                //开启事务
                operations.multi();
                //添加到关注的集合
                operations.opsForZSet().remove(followeeKey, entityId);
                //添加到粉丝的集合
                operations.opsForZSet().remove(followerKey, userId);

                //提交事务
                return operations.exec();
            }
        });
    }

    /**
     * 获取关注了多少实体
     * @param userId
     * @param entityType
     * @return
     */
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 获取粉丝数量
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 获取关注状态
     * @param userId
     * @param entityType
     * @param entityId
     * @return true为已关注，false为未关注
     */
    @Override
    public boolean findFollowStatus(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Double score = redisTemplate.opsForZSet().score(followeeKey, entityId);
        return score != null;
    }

    //关注的人的列表
    public List<Map<String, Object>> findFollowees(int userId, long offset, long limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if(targetIds == null){
            return null;
        }

        //存放关注的人的信息的列表
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            //关注的用户
            User user = userService.getById(id);
            map.put("user",user);
            //关注的时间
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //粉丝列表
    public List<Map<String, Object>> findFollowers(int userId, long offset, long limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if(targetIds == null){
            return null;
        }

        //存放粉丝的信息的列表
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : targetIds) {
            Map<String, Object> map = new HashMap<>();
            //粉丝
            User user = userService.getById(id);
            map.put("user",user);
            //粉丝关注的时间
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}

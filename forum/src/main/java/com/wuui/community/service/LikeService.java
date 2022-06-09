package com.wuui.community.service;

import org.springframework.stereotype.Service;

/**
 * @author Dee
 * @create 2022-05-22-13:04
 * @describe
 */
@Service
public interface LikeService {

    //点赞
    void like(int userId, int entityType, int entityId, int entityUserId);

    //赞数量
    Long findEntityLikeCount(int entityType, int entityId);

    //赞状态
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    //用户收到的赞
    int findUserLikeCount(int userId);
}

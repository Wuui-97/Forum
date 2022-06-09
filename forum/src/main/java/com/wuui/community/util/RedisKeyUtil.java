package com.wuui.community.util;

/**
 * @author Dee
 * @create 2022-05-22-12:56
 * @describe 生成redis-key的工具类
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:eneity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";
    private static final String PREFIX_POST = "post";


    //某实体的赞
    //键为：like:entity:entityType:entityId , 值为：set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    //用户的赞
    //键为：like:user:userId , 值为：int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    //用户的关注数量
    //键为：followee:userId:entityType , 值为：zset(entityId, date)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //实体的粉丝数量
    //键为：follower:entityType:entityId , 值为：zset(userId, date)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //验证码
    //键为:kaptcha:owner , 值为：String text
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    //登录凭证
    //键为：ticket:ticket , 值为：JSONString LoginTicket
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    //用户
    //键为：user:userId , 值为：JSONString User
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    //单日的独立访客
    //键为：uv:date , 值为：IP
    public static String getUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //区间的独立访客
    public static String getUVKey(String startDate, String endDate){
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    //单日的日活跃用户
    //键为：dau:date , 偏移量：userId
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT + date;
    }

    //区间的日活跃用户
    public static String getDAUKey(String startDate, String endDate){
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    //帖子分数
    //键为：post:score , 值为：set(postId)，用于保存定时任务quartz需要更新的帖子
    public static String getPostScoreKey(){
        return PREFIX_POST + SPLIT + "score";
    }

    public static String getPostPageHotKey(int pn, int limit){
        return PREFIX_POST + SPLIT + "hot" + pn + SPLIT + limit;
    }
}

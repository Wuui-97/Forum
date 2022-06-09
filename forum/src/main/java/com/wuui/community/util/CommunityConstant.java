package com.wuui.community.util;

/**
 * @author Dee
 * @create 2022-05-10-22:26
 * @describe 激活状态码
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    public final Integer ACTIVATION_SUCCESS = 0;

    /**
     * 激活重复
     */
    public final Integer ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    public final Integer ACTIVATION_FAILURE = 2;

    /**
     * 勾选rememberme的存活时间为30天
     */
    public final Integer REMEMBER_EXPIRED_SECONDS = 7 * 24 * 3600;

    /**
     * 默认存活时间为12小时
     */
    public final Integer DEFAULT_EXPIRED_SECONDS = 12 * 3600;

    /**
     * 实体类型：帖子
     */
    public final Integer ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    public final Integer ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    public final Integer ENTITY_TYPE_USER = 3;

    /**
     * 主题类型：评论
     */
    public final String TOPIC_COMMENT = "comment";

    /**
     * 主题类型：点赞
     */
    public final String TOPIC_LIKE = "like";

    /**
     * 主题类型：关注
     */
    public final String TOPIC_FOLLOW = "follow";

    /**
     * 主题类型：发布帖子
     */
    public final String TOPIC_PUBLISH = "publish";

    /**
     * 主题类型：删除帖子
     */
    public final String TOPIC_DELETE = "delete";

    /**
     * 系统用户
     */
    public final int SYSTEM_USER_ID = 1;

    /**
     * 分页显示的角标数量
     */
    public final Integer PAGE_DISPLAY_NUM = 5;

    /**
     * 权限：普通用户
     */
    public final String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    public final String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    public final String AUTHORITY_MODERATOR = "moderator";


}

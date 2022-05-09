package com.wuui.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuui.community.bean.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-08-22:55
 * @describe
 */
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //Param注解用于给参数取别名
    //如果只有一个参数，并且在《if》里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
}

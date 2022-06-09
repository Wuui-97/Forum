package com.wuui.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuui.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

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

    @Update("update discuss_post set comment_count = #{commentCount} where id = #{id}")
    void updateCommentCount(int id, int commentCount);

    @Update("update discuss_post set type = #{type} where id = #{id}")
    int updatetype(int id, int type);

    @Update("update discuss_post set status = #{status} where id = #{id}")
    int updateStatus(int id, int status);

    @Update("update discuss_post set score = #{score} where id = #{id}")
    int updateScore(int id, double score);
}

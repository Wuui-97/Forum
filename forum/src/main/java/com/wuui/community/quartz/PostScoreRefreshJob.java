package com.wuui.community.quartz;

import com.wuui.community.entity.DiscussPost;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.service.ElasticsearchService;
import com.wuui.community.service.LikeService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Dee
 * @create 2022-06-03-14:47
 * @describe
 */
@Slf4j
public class PostScoreRefreshJob implements Job, CommunityConstant {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    LikeService likeService;

    @Autowired
    ElasticsearchService elasticsearchService;

    @Autowired
    RedisTemplate redisTemplate;

    //论坛纪元
    private static final Date EPOCH;

    static {
        try {
            EPOCH = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-05-07 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化论坛纪元失败", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if(operations.size() == 0){
            log.info("没有需要更新的帖子！");
            return ;
        }

        log.info("[任务开始]：正在更新帖子分数" + operations.size());
        while (operations.size() > 0){
            this.refresh((Integer)operations.pop());
        }
        log.info("[任务完毕]：帖子更新完毕！");
    }

    private void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if(post == null){
            log.error("用户不存在，id = " + postId);
            return ;
        }

        //是否加精
        boolean isWonderful = post.getStatus() == 1;
        //评论数
        int commentCount = post.getCommentCount();
        //点赞数
        Long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);
        //权重
        long weight = isWonderful ? 70 : 0 + commentCount * 10 + likeCount * 10;
        //帖子分数 = log(weight) + (发布日期 - 论坛纪元)
        double score = Math.log10(Math.max(weight, 1)) + (post.getCreateTime().getTime() - EPOCH.getTime()) / (1000 * 3600 * 24);
        //更新帖子分数
        discussPostService.updateScore(postId, score);
        //更新es中的搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}

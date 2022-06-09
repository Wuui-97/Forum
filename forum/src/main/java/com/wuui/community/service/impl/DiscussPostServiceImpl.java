package com.wuui.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.wuui.community.entity.DiscussPost;
import com.wuui.community.dao.DiscussPostMapper;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.util.RedisKeyUtil;
import com.wuui.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Var;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Dee
 * @create 2022-05-09-12:48
 * @describe
 */
@Service
@Slf4j
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper,DiscussPost> implements DiscussPostService {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Autowired
    RedisTemplate redisTemplate;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    //某页帖子缓存
    private LoadingCache<String, Page<DiscussPost>> postPageCache;

    //一级缓存
    @PostConstruct
    public void init(){
        //初始化帖子页缓存
        postPageCache = Caffeine.newBuilder()
                //缓存的最大数量
                .maximumSize(maxSize)
                //最后一次写操作后经过指定时间过期
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Page<DiscussPost>>() {
                    @Nullable
                    @Override
                    public Page<DiscussPost> load(String key) throws Exception {
                        if(key == null || key.length() == 9){
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if(params == null || params.length == 0){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int pn = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);

                        //二级缓存
                        String redisKey = RedisKeyUtil.getPostPageHotKey(pn, limit);
                        Page<DiscussPost> postPage = getCacheHotPostPage(pn, limit);
                        if(postPage == null){
                            postPage = initCacheHotPostPage(pn, limit);
                        }

                        return postPage;
                    }
                });
    }

    @Override
    public Page<DiscussPost> findDiscussPosts(int userId, int pn, int limit, int orderMode) {

        if(userId == 0 && orderMode == 1){
            return postPageCache.get(pn + ":" + limit);
        }

        log.debug("----------load discussPost page from DB.-------");

        //页码为pn，大小为limit
        Page<DiscussPost> page = new Page<>(pn, limit);
        QueryWrapper<DiscussPost> wrapper = new QueryWrapper<>();
        wrapper.ne("status", 2)
                .eq(userId != 0, "user_id", userId)
                .orderByDesc("type")
                .orderByDesc(orderMode == 1, "score")
                .orderByDesc("create_time");

        return discussPostMapper.selectPage(page, wrapper);
    }

    //未使用
    @Override
    public int findDiscussPostRow(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    //将帖子信息进行转义及过滤后进行保存
    public int addDiscussPost(DiscussPost discussPost){
        if(discussPost == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //对敏感词进行过滤
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insert(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectById(id);
    }

    public int updateType(int id, int type){
        return discussPostMapper.updatetype(id, type);
    }

    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }

    /**
     * 利用Redis来缓存最热帖子页
     */
    //1.优先从Redis缓存中获取值
    private Page<DiscussPost> getCacheHotPostPage(int pn, int limit){
        String redisKey = RedisKeyUtil.getPostPageHotKey(pn, limit);
        return (Page<DiscussPost>) redisTemplate.opsForValue().get(redisKey);
    }

    //2.取不到时初始化Redis缓存
    private Page<DiscussPost> initCacheHotPostPage(int pn, int limit){
        //缓存中没有，先从数据库中查找
        log.debug("----------load discussPost page from DB.-------");

        //页码为pn，大小为limit
        Page<DiscussPost> page = new Page<>(pn, limit);
        QueryWrapper<DiscussPost> wrapper = new QueryWrapper<>();
        wrapper.ne("status", 2)
                .orderByDesc("type")
                .orderByDesc("score")
                .orderByDesc("create_time");
        Page<DiscussPost> postPage = discussPostMapper.selectPage(page, wrapper);

        //初始化保存到缓存中
        String redisKey = RedisKeyUtil.getPostPageHotKey(pn, limit);
        redisTemplate.opsForValue().set(redisKey, postPage, 3600, TimeUnit.SECONDS);

        return postPage;
    }

    //3.数据变更时清除缓存
    private void clearCacheHotPostPage(int pn, int limit){
        String redisKey = RedisKeyUtil.getPostPageHotKey(pn, limit);
        redisTemplate.delete(redisKey);
    }
}

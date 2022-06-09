package com.wuui.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuui.community.entity.Comment;
import com.wuui.community.dao.CommentMapper;
import com.wuui.community.dao.DiscussPostMapper;
import com.wuui.community.service.CommentService;
import com.wuui.community.util.CommunityConstant;
import com.wuui.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * @author Dee
 * @create 2022-05-17-18:23
 * @describe
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService, CommunityConstant {

    @Autowired
    CommentMapper commentMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Override
    public IPage<Comment> findCommentPageByEntity(int entityType, int entityId, int pn, int limit) {
        //pn：当前页 , 每页显示limit条评论
        Page<Comment> page = new Page<>(pn,limit);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type",entityType)
                    .eq("entity_id",entityId)
                    .orderByAsc("create_time");
        IPage<Comment> commentIPage = commentMapper.selectPage(page,queryWrapper);

        return commentIPage;
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("entity_type",entityType)
                    .eq("entity_id",entityId);
        return commentMapper.selectCount(queryWrapper);
    }

    //增加评论和更新评论数量涉及两张表，需要对其进行事务管理
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //对评论内容进行html过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //进行敏感词过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        //将评论内容插入到数据库中
        int row = commentMapper.insert(comment);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int commentCount = findCommentCount(ENTITY_TYPE_POST, comment.getEntityId());
            //更新评论数量
            discussPostMapper.updateCommentCount(comment.getEntityId(), commentCount);
        }
        return row;
    }

    public Comment findCommentById(int id){
        return commentMapper.selectById(id);
    }
}

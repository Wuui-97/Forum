package com.wuui.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuui.community.entity.Comment;
import org.springframework.stereotype.Service;

/**
 * @author Dee
 * @create 2022-05-17-18:22
 * @describe
 */
@Service
public interface CommentService extends IService<Comment> {

    IPage<Comment> findCommentPageByEntity(int entityType, int entityId, int pn, int limit);

    int findCommentCount(int entityType, int entityId);

    int addComment(Comment comment);

    Comment findCommentById(int id);

}

package com.wuui.community.service;

import com.wuui.community.entity.DiscussPost;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-29-18:24
 * @describe
 */
@Service
public interface ElasticsearchService {

    //es保存discusspost
    public void saveDiscussPost(DiscussPost discussPost);

    //es删除discusspost
    public void deleteDiscussPost(int id);

    //es查询discusspost
    public Map<String, Object> searchDiscussPost(String keyword, int current, int limit) throws IOException;

}

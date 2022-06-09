package com.wuui.community.dao.elaticsearch;

import com.wuui.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dee
 * @create 2022-05-29-14:09
 * @describe
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}

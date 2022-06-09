package com.wuui.community.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wuui.community.dao.elaticsearch.DiscussPostRepository;
import com.wuui.community.entity.DiscussPost;
import com.wuui.community.service.DiscussPostService;
import com.wuui.community.service.ElasticsearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Dee
 * @create 2022-05-29-18:31
 * @describe
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    DiscussPostRepository discussPostRepository;

    @Qualifier("client")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    DiscussPostService discussPostService;

    //es保存discusspost
    public void saveDiscussPost(DiscussPost discussPost) {
        discussPostRepository.save(discussPost);
    }

    //es删除discusspost
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    //es查询discusspost
    public Map<String, Object> searchDiscussPost(String keyword, int current, int limit) throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");

        //高亮配置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //构造搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                //multiMatch对title和content联合搜索
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(current)  //指定查询的起始位置
                .size(limit)    //指定显示的记录数
                .highlighter(highlightBuilder); //高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("total", search.getHits().getTotalHits().value);

        List<DiscussPost> postList = new LinkedList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);

            //处理高亮显示的结果
            HighlightField title = hit.getHighlightFields().get("title");
            if (title != null) {
                discussPost.setTitle(title.getFragments()[0].toString());
            }
            HighlightField content = hit.getHighlightFields().get("content");
            if (content != null) {
                discussPost.setContent(content.getFragments()[0].toString());
            }

//            System.out.println(discussPost);
            postList.add(discussPost);
        }

        postMap.put("postList", postList);

        return postMap;
    }

}

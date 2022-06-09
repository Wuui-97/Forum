package com.wuui.community;

import com.alibaba.fastjson.JSONObject;
import com.wuui.community.dao.DiscussPostMapper;
import com.wuui.community.dao.elaticsearch.DiscussPostRepository;
import com.wuui.community.entity.DiscussPost;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dee
 * @create 2022-05-29-14:14
 * @describe
 */
@SpringBootTest
public class ElasticSerachTests {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    DiscussPostRepository discussPostRepository;

    @Qualifier("client")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectById(238));
        discussPostRepository.save(discussPostMapper.selectById(239));
        discussPostRepository.save(discussPostMapper.selectById(240));
    }

    @Test
    public void testInsertMore(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testDelete(){
        discussPostRepository.delete(discussPostMapper.selectById(240));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectById(238);
        post.setStatus(1);
        discussPostRepository.save(post);
    }

    /**
     * 不带高亮显示
     * @throws IOException
     */
    @Test
    public void testSearchNoHighLight() throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");

        //构造搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                //multiMatch对title和content联合搜索
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)  //指定查询的起始位置
                .size(10);  //指定显示的记录数

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSONObject.toJSON(search));

        List<DiscussPost> list = new LinkedList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);
            System.out.println(discussPost);
            list.add(discussPost);
        }
    }

    /**
     * 带高亮显示
     * @throws IOException
     */
    @Test
    public void testSearchWithHighLight() throws IOException {
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
                .query(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .from(0)  //指定查询的起始位置
                .size(10) //指定显示的记录数
                .highlighter(highlightBuilder); //高亮

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        long total = search.getHits().getTotalHits().value;
        System.out.println("总数为：" + total);

        List<DiscussPost> list = new LinkedList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            DiscussPost discussPost = JSONObject.parseObject(hit.getSourceAsString(), DiscussPost.class);

            //处理高亮显示的结果
            HighlightField title = hit.getHighlightFields().get("title");
            if(title != null){
                discussPost.setTitle(title.getFragments()[0].toString());
            }
            HighlightField content = hit.getHighlightFields().get("content");
            if(content != null){
                discussPost.setContent(content.getFragments()[0].toString());
            }

            System.out.println(discussPost);
            list.add(discussPost);

        }
    }

}

package com.nowcoder.community.community;

import com.alibaba.fastjson.JSON;
import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private  ElasticsearchRestTemplate restTemplate;


    @Test
    public void testInsert(){
        //插入一条数据
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
    }

    @Test
    public void testUpdate(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        discussPostRepository.save(post);
    }

    @Test
    public void testDelete(){
        discussPostRepository.deleteById(231);
    }

    @Test
    public void testSearchByRepository(){
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order((SortOrder.DESC)))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0,10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();


        Pageable pageable = PageRequest.of(0, 10);

        SortBuilder<FieldSortBuilder> sortBuilder = new FieldSortBuilder("type")
                .order(SortOrder.DESC);

//        NativeSearchQuery query = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.matchQuery("title", "互联网寒冬"))
//                .withPageable(pageable)
//                .withSort(sortBuilder)
//                .build();
        SearchHits<DiscussPost> searchHits = restTemplate.search(query, DiscussPost.class);
        System.out.println(searchHits.getTotalHits());
        List<Map<String, Object>> list = new ArrayList<>();
        String title = null;
        for (SearchHit<DiscussPost> post : searchHits){
            System.out.println(post);
//            System.out.println(post.getContent().getClass());
            Map<String,Object> map = new HashMap<>();
            List<String> highlightField = post.getHighlightField("title");
            if (highlightField != null)
            {
                String s = highlightField.get(1);
                System.out.println("------>" + s);
            }
//            if (post.getHighlightField("title") == null){
//                map.put("title",post.getContent().getTitle());
////                System.out.println(map);
//            }else {
//                map.put("title",post.getHighlightField("title"));
//            }
////            System.out.println("----------------------------");
//            if (post.getHighlightField("title") == null){
//                map.put("content",post.getContent().getContent());
//            }else {
//                map.put("content",post.getHighlightField("content"));
//            }
            list.add(map);
//            System.out.println(list);
        }
//        System.out.println(list);
    }

    @Test
    public void testHighLight(){

        String preTag = "<font color='red'>";
        String postTag = "</font>";

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "互联网寒冬"))
                .withHighlightFields(new HighlightBuilder.Field("title")
                        .preTags(preTag)
                        .postTags(postTag))
                .build();
        SearchHits<DiscussPost> searchHits = restTemplate.search(query, DiscussPost.class);


        List<SearchHit<DiscussPost>> searchHitList = searchHits.getSearchHits();
        List<Map<String, Object>> hlList = new ArrayList<>();
        for (SearchHit h : searchHitList) {

            List<String> highlightField = h.getHighlightField("title");
            String nameValue = highlightField.get(0);

            String originalJson = JSON.toJSONString(h.getContent());
            Map<String, Object> myHighLight = (Map<String, Object>) JSON.parse(originalJson);
            // 用高亮的搜索结果覆盖原字段值
            myHighLight.put("title", nameValue);
            System.out.println(myHighLight);

            hlList.add(myHighLight);
        }
    }

}

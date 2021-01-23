package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //search?keyword=xxx
    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        //搜索帖子
        SearchHits<DiscussPost> searchResult =
        elasticsearchService.searchDiscussPost(keyword,page.getCurrent() - 1,page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (searchResult != null){
            for (SearchHit<DiscussPost> searchHit : searchResult){
                Map<String,Object> map = new HashMap<>();
                //帖子
                map.put("post",searchHit.getContent());
                //高亮内容
                String title = StringUtils.strip(searchHit.getHighlightField("title").toString(),"[]");
                //如果title没有匹配则不放入高亮内容
                map.put("highlightTitle",title.equals("")?searchHit.getContent().getTitle():title);
                String content = StringUtils.strip(searchHit.getHighlightField("content").toString(),"[]");
                map.put("highlightContent",content);
                //作者
                map.put("author",userService.findUserById(searchHit.getContent().getUserId()));
                //点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,searchHit.getContent().getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);

        //分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalHits());

        return "/site/search";
    }

}

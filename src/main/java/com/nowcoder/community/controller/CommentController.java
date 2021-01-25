package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer producer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    @ResponseBody
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        String postId = String.valueOf(discussPostId);

        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",postId);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            List<Comment> target = commentService.findCommentByEntityId(comment.getEntityId());
            event.setEntityUserId(target.get(0).getUserId());
        }
        producer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST){
            //触发评论帖子事件，不是评论里面的回复  ES
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            producer.fireEvent(event);
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }


        return CommunityUtil.getJSONString(0,"发布成功！");

    }



    @RequestMapping(path = "/addEnd/{discussPostId}",method = RequestMethod.POST)
    public String addCommentEnd(@PathVariable("discussPostId")int discussPostId, Comment comment){
        int rows = discussPostService.countDiscussPostTotalRows(discussPostId);
        int page = 0;
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        if ((rows + 1)%5 != 0){
            page = rows / 5 + 1;
        }else {
            page = rows / 5;
        }

        String postId = String.valueOf(discussPostId);

        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",postId);
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
            List<Comment> target = commentService.findCommentByEntityId(comment.getEntityId());
            event.setEntityUserId(target.get(0).getUserId());
        }

        producer.fireEvent(event);

        //计算帖子分数
        String redisKey = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(redisKey,discussPostId);

        //跳转到最后一页
        return "redirect:/discuss/detail/" + discussPostId + "?current=" + page;
    }

}

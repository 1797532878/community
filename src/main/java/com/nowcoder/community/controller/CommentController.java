package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add/{discussPostId}",method = RequestMethod.POST)
    @ResponseBody
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

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
        //跳转到最后一页
        return "redirect:/discuss/detail/" + discussPostId + "?current=" + page;
    }

}

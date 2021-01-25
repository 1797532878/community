package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.*;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    private static  final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    //上传头像
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error","您还没有选择图片！");
            return "/site/setting";
        }

        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！");
            return "/site/setting";
        }
        //生成随机文件名
        String fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest =  new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败： " + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!",e);
        }
        //更新当前用户的头像路径(Web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath + "/" + fileName;
        //向浏览器输出图片
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);
        try (ServletOutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(fileName);)//这样写会自动关闭
        { byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e);
        }
    }

    //更新密码
    @RequestMapping(path = "/update/password",method = RequestMethod.POST)
    public String updatePassword(String oldPassword,String newPassword,String newPassword2,Model model,
                                 @CookieValue("ticket")String ticket){
        User user = hostHolder.getUser();
        if (!(CommunityUtil.md5(oldPassword + user.getSalt())).equals(user.getPassword())){
            model.addAttribute("passwordError","原始密码输出错误");
            return "/site/setting";
        }

        if (newPassword == null){
            model.addAttribute("passwordError","密码不能为空");
            return "/site/setting";
        }
        if (StringUtils.length(newPassword) < 8){
            model.addAttribute("newPasswordError","密码不能少于8位");
            return "/site/setting";
        }
        if (!(newPassword.equals(newPassword2))){
            model.addAttribute("newPasswordError_2","两次输入密码不一致");
            return "/site/setting";
        }
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updateUserPassword(user.getId(),newPassword);
        userService.logout(ticket);
        return "redirect:/login";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId")int userId,Model model){

        User user = userService.findUserById(userId);
        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }

        //用户
        model.addAttribute("user",user);
        //点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowCount(ENTITY_TYPE_USER,userId);
        model.addAttribute("followerCount",followerCount);
        //是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null){
            //实体ID 是这个空间的userId
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);
        return "/site/profile";
    }


    //我的帖子
    @RequestMapping(path = "/post/{userId}",method = RequestMethod.GET)
    private String getPostPage(@PathVariable("userId")int userId, Model model, Page page){

        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        page.setPath("/user/post/" + userId);
        page.setLimit(5);
        //帖子总数
        int allPosts = discussPostService.findDiscussPostRows(userId);
        page.setRows(allPosts);

        model.addAttribute("allPosts",allPosts);

        List<Map<String,Object>> list = new ArrayList<>();
        //分页查询我的帖子
        List<DiscussPost> postList = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(),0);
        for (DiscussPost discussPost : postList){
            Map<String,Object> map = new HashMap<>();
            map.put("post",discussPost);
            //查询该帖子的赞
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
            map.put("likeCount",likeCount);
            list.add(map);
        }
        model.addAttribute("postsList",list);
        return "/site/my-post";
    }

    //我的回复
    @RequestMapping(path = "/reply/{userId}",method = RequestMethod.GET)
    private String getReplyPage(@PathVariable("userId")int userId,Page page,Model model){
        User user = userService.findUserById(userId);
        model.addAttribute("user",user);
        page.setPath("/user/reply/" + userId);
        page.setLimit(5);
        //查询回复的帖子数
        int commentCounts = commentService.findCountByEntityTypeAndUserId(ENTITY_TYPE_POST, userId);
        page.setRows(commentCounts);

        model.addAttribute("commentCounts",commentCounts);

        List<Map<String,Object>> list = new ArrayList<>();
        //分页查询帖子
        List<Comment> commentList = commentService.findCommentByEntityTypeAndUserId(ENTITY_TYPE_POST,userId,page.getOffset(),page.getLimit());
        for (Comment comment:commentList){
            Map<String,Object> map = new HashMap<>();
            DiscussPost postById = discussPostService.findDiscussPostById(comment.getEntityId());
            if (postById != null){
                map.put("comment",comment);
                map.put("title",postById.getTitle());
                list.add(map);
            }
        }
        model.addAttribute("commentList",list);
        return "/site/my-reply";
    }
}

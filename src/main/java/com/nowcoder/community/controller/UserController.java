package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
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

@Controller
@RequestMapping("/user")
public class UserController {

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
}

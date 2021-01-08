package com.nowcoder.community.community;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    //主动调用thymeleaf
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testEmail(){
        mailClient.senMail("695935391@qq.com","test","welcome");
    }

    @Test
    public void testHtmlMail(){

        Context context = new Context();
        //给页面传参 使用context处理
        context.setVariable("username","sunday");
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.senMail("695935391@qq.com","html",content);
    }

}

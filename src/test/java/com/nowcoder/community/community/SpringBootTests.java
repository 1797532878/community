package com.nowcoder.community.community;

import com.nowcoder.community.CommunityApplication;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    @BeforeEach
    public  void beforeClass(){
        System.out.println("BeforeTest");
    }
    @AfterEach
    public  void afterClass(){
        System.out.println("afterTest");
    }

    @BeforeAll
    public static void before(){
        System.out.println("beforeAll");
    }
    @AfterAll
    public static void after(){
        System.out.println("afterAll");
    }

    @Test
    public void test1(){
        System.out.println("test1");
    }
}

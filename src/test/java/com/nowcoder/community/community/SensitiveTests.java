package com.nowcoder.community.community;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以赌博，可以嫖☆☆a啊啊啊嘿娼，可以吸毒☆，可以开票啊，哈哈哈！开票啊";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);
    }
}

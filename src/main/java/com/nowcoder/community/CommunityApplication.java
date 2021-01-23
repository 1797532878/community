package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
//@EnableElasticsearchRepositories(basePackages = "com.nowcoder.community")
public class CommunityApplication {

    @PostConstruct
    public void init(){
        //解决netty启动冲突的问题
        //NettyRuntime--->setAvailableProcessors---if (this.availableProcessors != 0)会报错
        //Netty4Utils 里面调用了这个，但是redis之前已经调用了，导致报错 see---> setAvailableProcessors
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}

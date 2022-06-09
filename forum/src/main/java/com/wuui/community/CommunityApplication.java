package com.wuui.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@MapperScan("com.wuui.community.dao")
@SpringBootApplication
public class CommunityApplication {

    @PostConstruct
    public void init(){
        //解决Netty启动冲突问题
        //见 Netty4Utils.setAvailableProcessors
        System.setProperty("es.set.netty.runtime.available.processors", "fasle");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}

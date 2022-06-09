package com.wuui.community;

import com.wuui.community.service.TestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Dee
 * @create 2022-05-16-21:35
 * @describe
 */
@SpringBootTest
public class TransactionTest {

    @Autowired
    TestService testService;

    @Test
    @DisplayName("测试事务的传播行为")
    public void transaction1(){
        testService.addDiscussPost();
    }


}

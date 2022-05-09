package com.wuui.community;

import com.wuui.community.bean.User;
import com.wuui.community.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@DisplayName("测试类")
@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Test
    @DisplayName("测试MyBatis")
    void contextLoads() {
        User user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

}

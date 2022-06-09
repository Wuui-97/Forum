package com.wuui.community;

import com.wuui.community.entity.User;
import com.wuui.community.dao.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Test
    public void testDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(df.format(new Date()));
    }

}

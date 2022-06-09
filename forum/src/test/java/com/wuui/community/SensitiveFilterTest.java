package com.wuui.community;

import com.wuui.community.util.SensitiveFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Dee
 * @create 2022-05-15-16:05
 * @describe
 */
@SpringBootTest
public class SensitiveFilterTest {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    @DisplayName("测试敏感词过滤")
    void sensitiveFilterTest(){
        String text = "&*&赌&&博（（（你猜我要输入什么***会有***99嫖&&&娼888吗？";
        String filterText = sensitiveFilter.filter(text);
        System.out.println(filterText);
    }

}

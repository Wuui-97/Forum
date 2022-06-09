package com.wuui.community;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuui.community.entity.Message;
import com.wuui.community.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Dee
 * @create 2022-05-19-19:41
 * @describe
 */
@SpringBootTest
public class MapperTest {

    @Autowired
    MessageService messageService;

    @Test
    public void messageTest(){
        Page<Message> page = messageService.findConversations(111, 1, 5);
        List<Message> records = page.getRecords();
        for (Message message : records) {
            System.out.println(message);
        }

        System.out.println(messageService.findConversaionCount(111));

//        List<Message> records = messageService.findLetters("111_112", 0, 5).getRecords();
//        for (Message record : records) {
//            System.out.println(record);
//        }
//        System.out.println(messageService.findLetterCount("111_112"));
//        System.out.println(messageService.findLetterUnreadCount(111, "111_112"));
    }

}

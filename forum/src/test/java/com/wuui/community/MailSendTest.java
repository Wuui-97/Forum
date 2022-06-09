package com.wuui.community;

import com.wuui.community.util.MailClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Dee
 * @create 2022-05-09-22:21
 * @describe
 */
@SpringBootTest
public class MailSendTest {

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    @Test
    @DisplayName("测试发送普通text")
    public void textMailTest(){
        mailClient.sendMail("15001753396@163.com","JavaSendMail","正常");
    }

    @Test
    @DisplayName("测试HTML发送邮件")
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "Dee");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("15001753396@163.com", "HTML", content);
    }

}

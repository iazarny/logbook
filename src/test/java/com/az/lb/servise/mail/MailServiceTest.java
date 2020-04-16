package com.az.lb.servise.mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.templatemode.TemplateMode;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Test
    void getTemplate() throws Exception {
        assertEquals("I am test" , mailService.getTemplate("testkey", null, "test1.txt"));
    }

    @Test
    void composeText() throws Exception {

        String template = "User: [(${name})]";
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("name", "HAL9000");
        }};
        String result = mailService.compose(template, data, TemplateMode.TEXT, null);
        assertEquals("User: HAL9000", result);

    }

    @Test
    void composeHtml() throws Exception {

        String template = "<a th:text=\"${age}\" th:href=\"${name}\"></a>";
        template += "<table><tr><td th:text=\"'Organization: ' + ${orgName}\"></td></tr></table>";
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("name", "HAL9000");
            put("age", "1234567890");
            put("orgName", "Junk");
        }};
        String result = mailService.compose(template, data, TemplateMode.HTML, null);
        assertEquals("<a href=\"HAL9000\">1234567890</a><table><tr><td>Organization: Junk</td></tr></table>", result);

    }

    @Test
    void composeMessage() throws Exception {

        final JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        final MimeMessage message = sender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        mailService.composeMessage(
                helper,
                "testcompose",
                "han.solo@starwars.com",
                "jabba.hutt@starwars.com",
                "Hi tinker!",
                new HashMap<String, Object>() {{
                    put("at", "2012-10-10 18:30");
                }},
                null,
                "tofriend"
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        helper.getMimeMessage().writeTo(byteArrayOutputStream);
        String str = byteArrayOutputStream.toString("UTF-8");


        assertTrue(
                str.contains("<td>2012-10-10 18:30</td>")
        );

        assertTrue(
                str.contains("I'll visit you 2012-10-10 18:30")
        );



    }


}
package com.az.lb.servise.mail;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Service
public class MailService {

    public static String MAIL_REGISTER = "register";
    public static String MAIL_FORGOTPWD = "forgot";
    public static String MAIL_INVITE = "invite";

    private static String PREFIX = "mail";

    @Value("${lb.mail.from}")
    private String lbMailFrom;

    @Autowired
    private JavaMailSender javaMailSender;

    TemplateEngine textTemplateEngine = new TemplateEngine();
    TemplateEngine htmlTemplateEngine = new TemplateEngine();
    StringTemplateResolver textStringTemplateResolver = new StringTemplateResolver();
    StringTemplateResolver htmlStringTemplateResolver = new StringTemplateResolver();

    public MailService() {
        textStringTemplateResolver.setTemplateMode(TemplateMode.TEXT);
        htmlStringTemplateResolver.setTemplateMode(TemplateMode.HTML);
        this.textTemplateEngine.setTemplateResolver(textStringTemplateResolver);
        this.htmlTemplateEngine.setTemplateResolver(htmlStringTemplateResolver);
    }

    public void send(String mailKey, String to, String subject, Map<String, Object> data, String locale) {
        try {
            final MimeMessage message = javaMailSender.createMimeMessage();
            System.out.println(">>> 1 " + new Date());
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            System.out.println(">>> 2 " + new Date());
            composeMessage(
                    helper,
                    mailKey,
                    lbMailFrom,
                    to,
                    subject,
                    data,
                    locale,
                    mailKey
            );
            System.out.println(">>> 3 " + new Date());
            javaMailSender.send(message);
            System.out.println(">>> 4 " + new Date());
        } catch (Exception e) {
            e.printStackTrace(); //todo
        }
    }

    void composeMessage(final MimeMessageHelper helper,
                        final String mailKey,
                        final String from,
                        final String to,
                        final String subject,
                        final Map<String, Object> model,
                        final String locale,
                        final String templateName) throws Exception {

        helper.setTo(to);
        helper.setFrom(from);
        helper.setSentDate(new Date());
        helper.setSubject(subject);

        String textContent = null;
        String textTemplate = getTemplate(mailKey, locale, templateName + ".txt");
        if (textTemplate != null) {
            textContent = compose(textTemplate, model, TemplateMode.TEXT, locale);
        }


        String htmlContent = null;
        String htmlTemplate = getTemplate(mailKey, locale, templateName + ".html");
        if (htmlTemplate != null) {
            htmlContent = compose(htmlTemplate, model, TemplateMode.HTML, locale);
        }


        if (textContent != null && htmlContent == null) {
            helper.setText(textContent, false);
        } else if (textContent == null && htmlContent != null) {
            helper.setText(htmlContent, true);
        } else if (textContent != null && htmlContent != null) {
            helper.setText(textContent, htmlContent);
        } else {
            throw new Exception("Both templates are nor found. Name is " + templateName);
        }


    }


    String compose(String template, Map<String, Object> data, TemplateMode templateMode, String slocale) {
        final String rez;
        final Locale locale = new Locale(ObjectUtils.defaultIfNull(slocale, "en"));

        if (TemplateMode.TEXT == templateMode) {
            rez = textTemplateEngine.process(template, new Context(locale, data));
        } else {
            rez = htmlTemplateEngine.process(template, new Context(locale, data));
        }

        return rez;
    }

    String getTemplate(String mailKey, String locale, String template) {

        try {
            String currLocale = ObjectUtils.defaultIfNull(locale, "en");

            Path path = Paths.get(PREFIX, mailKey, currLocale, template);

            String content = FileUtils.readFileToString(
                    ResourceUtils.getFile("classpath:" + path.toString()),
                    StandardCharsets.UTF_8);

            return content;

        } catch (Exception e) {

            e.printStackTrace(); //todo log

        }

        return null;


    }


}

package com.az.lb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {

    @Value("${lb.mail.sender.host}")
    private String lbMailSenderHost;

    @Value("${lb.mail.sender.port}")
    private int lbMailSenderPort;

    @Value("${lb.mail.sender.username}")
    private String lbMailSenderUsername;

    @Value("${lb.mail.sender.password}")
    private String lbMailSenderPassword;

    @Value("${mail.transport.protocol}")
    private String mailTransportProtocol;

    @Value("${mail.smtp.auth}")
    private boolean mailSmtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean mailSmtpStarttlsEnable;

    @Value("${mail.debug}")
    private boolean mailDebug;

    @Bean
    public JavaMailSender getJavaMailSender()
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(lbMailSenderHost);
        mailSender.setPort(lbMailSenderPort);

        if (mailSmtpAuth) {
            mailSender.setUsername(lbMailSenderUsername);
            mailSender.setPassword(lbMailSenderPassword);
        }



        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailTransportProtocol);
        props.put("mail.smtp.auth", mailSmtpAuth);
        props.put("mail.smtp.starttls.enable", mailSmtpStarttlsEnable);
        props.put("mail.debug", mailDebug);

        return mailSender;
    }
}

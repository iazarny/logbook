package com.az.lb.servise.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

public class MailSendJob implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(MailSendJob.class);

    private final JavaMailSender sender;
    private final MimeMessage message;

    public MailSendJob(final JavaMailSender sender, final MimeMessage message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            sender.send(message);
        } catch (Exception ex) {
            logger.error("Cannot send email", ex);
        }
    }

}

package com.az.lb.servise.mail;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

public class MailSendJob implements Runnable {

    private final JavaMailSender sender;
    private final MimeMessage message;

    public MailSendJob(final JavaMailSender sender, final MimeMessage message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public void run() {
        sender.send(message);
        System.out.println(">>> Masg ok ");
    }

}

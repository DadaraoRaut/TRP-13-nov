package com.erp.admin_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCredentials(String to, String id, String password, String type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("ERP Credentials - " + type);
        message.setText("Your " + type + " ID: " + id + "\nPassword: " + password + "\n\nLogin with these credentials.");
        mailSender.send(message);
    }
}

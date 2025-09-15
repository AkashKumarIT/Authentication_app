package com.auth.authApp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail,String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Authentication Service Platform");
        message.setText("Hello "+name+",\n\nThanks for registering with us!\n\nBest Regards,\nAuthentication Service Team");
        mailSender.send(message);
    }

    public void sendResetOtpEmail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for resetting password is "+otp+" use this as otp to proceed with resetting your password");
        mailSender.send(message);
    }
}

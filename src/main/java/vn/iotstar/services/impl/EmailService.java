package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendOTPEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bumngo364@gmail.com");  // Thay bằng email của bạn
        message.setTo(to);
        message.setSubject("Your OTP Code for Registration");
        message.setText("Your OTP code is: " + otp);
        emailSender.send(message);
    }
}


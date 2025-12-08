package com.example.spendwise.infrastructure.email;

import com.example.spendwise.application.dtos.email.ContactFormDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String from;

    @Value("${app.email.contact.recipient}")
    private String contactRecipient;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public int generateSixDigitCode() {
        return ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
    }

    public void sendEmailVerificationCode(String to, int code) {
        String subject = "SpendWise - Email verification";
        String body = "Your verification code is: " + code + "\n\nIf you didn't request this, ignore this message.";
        sendSimpleEmail(to, subject, body);
    }

    public void sendPasswordResetCode(String to, int code) {
        String subject = "SpendWise - Password reset code";
        String body = "Your password reset code is: " + code + "\nThis code expires in a short time.";
        sendSimpleEmail(to, subject, body);
    }

    public void sendContactFormEmail(ContactFormDto dto) {
        String subject = "SpendWise Contact Form: " + dto.getSubject();
        StringBuilder body = new StringBuilder();
        body.append("From: ").append(dto.getName()).append(" <").append(dto.getEmail()).append(">\n\n");
        body.append(dto.getMessage());
        sendSimpleEmail(contactRecipient, subject, body.toString());
    }

    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email", ex);
        }
    }
}

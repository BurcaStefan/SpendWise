package com.example.spendwise.controllers;

import com.example.spendwise.infrastructure.email.EmailService;
import com.example.spendwise.application.dtos.email.ContactFormDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-password-reset-code")
    public ResponseEntity<Integer> sendPasswordResetCode(@RequestParam String email) {
        int code = emailService.generateSixDigitCode();
        emailService.sendPasswordResetCode(email, code);
        return ResponseEntity.ok(code);
    }

    @PostMapping("/send-email-verification-code")
    public ResponseEntity<Integer> sendEmailVerificationCode(@RequestParam String email) {
        int code = emailService.generateSixDigitCode();
        emailService.sendEmailVerificationCode(email, code);
        return ResponseEntity.ok(code);
    }

    @PostMapping("/send-contact-form")
    public ResponseEntity<Void> sendContactForm(@RequestBody @Validated ContactFormDto dto) {
        emailService.sendContactFormEmail(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

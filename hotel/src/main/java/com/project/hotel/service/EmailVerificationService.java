package com.project.hotel.service;

import com.project.hotel.dto.internal.OTPEntry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmailVerificationService {
    JavaMailSender mailSender;

    private final Map<String, OTPEntry> verificationCodes = new HashMap<>();
    private static final long EXPIRE_DURATION = 60 * 1000;

    public void sendVerificationCode(String email) {
        String code = generateCode();
        long expireAt = System.currentTimeMillis() + EXPIRE_DURATION;
        verificationCodes.put(email, new OTPEntry(code, expireAt));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your verification code");
        message.setText("Your verification code is: " + code + "\nCode will expire in 1 minutes.");
        message.setFrom("your-email@gmail.com");
        mailSender.send(message);
    }

    public boolean verifyCode(String email, String code) {
        OTPEntry entry = verificationCodes.get(email);
        if (entry == null) return false;
        if (System.currentTimeMillis() > entry.getExpireAt()) {
            verificationCodes.remove(email);
            return false;
        }
        return entry.getCode().equalsIgnoreCase(code);
    }
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}

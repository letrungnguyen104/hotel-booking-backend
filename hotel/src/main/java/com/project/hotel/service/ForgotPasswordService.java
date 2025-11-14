package com.project.hotel.service;

import com.project.hotel.dto.request.ResetPasswordRequest;
import com.project.hotel.dto.request.SendOtpRequest;
import com.project.hotel.dto.request.VerifyOtpRequest;
import com.project.hotel.dto.response.ForgotPasswordResponse;
import com.project.hotel.entity.OtpToken;
import com.project.hotel.entity.User;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.OtpTokenRepository;
import com.project.hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {
    
    OtpTokenRepository otpTokenRepository;
    UserRepository userRepository;
    JavaMailSender mailSender;
    PasswordEncoder passwordEncoder;
    
    private static final long OTP_EXPIRE_MINUTES = 5;
    
    @Transactional
    public ForgotPasswordResponse sendOtp(SendOtpRequest request) {
        String email = request.getEmail();
        
        // Kiểm tra email có tồn tại trong hệ thống không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Tạo OTP code
        String otpCode = generateOtpCode();
        
        // Lưu OTP vào database
        OtpToken otpToken = OtpToken.builder()
                .email(email)
                .otpCode(otpCode)
                .createdAt(LocalDateTime.now())
                .expireAt(LocalDateTime.now().plusMinutes(OTP_EXPIRE_MINUTES))
                .verified(false)
                .used(false)
                .build();
        
        otpTokenRepository.save(otpToken);
        
        // Gửi email chứa OTP
        sendOtpEmail(email, otpCode);
        
        log.info("OTP sent to email: {}", email);
        
        return ForgotPasswordResponse.builder()
                .message("Verification code has been sent to your email")
                .email(email)
                .build();
    }
    
    @Transactional
    public ForgotPasswordResponse verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        
        // Tìm OTP token mới nhất của email
        OtpToken otpToken = otpTokenRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        
        // Kiểm tra OTP đã hết hạn chưa
        if (LocalDateTime.now().isAfter(otpToken.getExpireAt())) {
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }
        
        // Kiểm tra OTP đã được sử dụng chưa
        if (otpToken.isUsed()) {
            throw new AppException(ErrorCode.OTP_ALREADY_USED);
        }
        
        // Kiểm tra OTP code có đúng không
        if (!otpToken.getOtpCode().equals(code)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        
        // Đánh dấu OTP đã được xác thực
        otpToken.setVerified(true);
        otpTokenRepository.save(otpToken);
        
        log.info("OTP verified for email: {}", email);
        
        return ForgotPasswordResponse.builder()
                .message("Verification code verified successfully")
                .email(email)
                .build();
    }
    
    @Transactional
    public ForgotPasswordResponse resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        String newPassword = request.getPassword();
        
        // Tìm OTP token đã được xác thực, chưa sử dụng và chưa hết hạn
        OtpToken otpToken = otpTokenRepository
                .findByEmailAndOtpCodeAndVerifiedTrueAndUsedFalseAndExpireAtAfter(
                        email, code, LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_OTP));
        
        // Tìm user theo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Đánh dấu OTP đã được sử dụng
        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
        
        // Đánh dấu tất cả OTP khác của email này là đã sử dụng
        otpTokenRepository.markAllAsUsedByEmail(email);
        
        log.info("Password reset successfully for email: {}", email);
        
        return ForgotPasswordResponse.builder()
                .message("Password has been reset successfully")
                .email(email)
                .build();
    }
    
    private void sendOtpEmail(String email, String otpCode) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Reset - Verification Code");
            message.setText(String.format(
                    "Your password reset verification code is: %s\n\n" +
                    "This code will expire in %d minutes.\n\n" +
                    "If you did not request this password reset, please ignore this email.",
                    otpCode, OTP_EXPIRE_MINUTES
            ));
            message.setFrom("noreply@hotel.com");
            
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
    
    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(900000) + 100000; // 6 digit code
        return String.valueOf(number);
    }
    
    // Tự động xóa các OTP đã hết hạn mỗi giờ
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredOtpTokens() {
        otpTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Cleaned up expired OTP tokens");
    }
}

package com.project.hotel.service;

import com.project.hotel.entity.AuditLog;
import com.project.hotel.entity.User;
import com.project.hotel.repository.AuditLogRepository;
import com.project.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public void saveLog(User user, String action, String details) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .details(details)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    @Async
    @Transactional
    public void saveLog(String username, String action, String details) {
        User user = userRepository.findByUsername(username).orElse(null);
        saveLog(user, action, details);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getLogsByUser(int userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
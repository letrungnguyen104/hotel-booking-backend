package com.project.hotel.aspect;

import com.project.hotel.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "@annotation(logActivity)", returning = "result")
    public void logActivity(JoinPoint joinPoint, LogActivity logActivity, Object result) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return;
            }
            String username = auth.getName();
            String action = logActivity.value();
            String methodName = joinPoint.getSignature().getName();
            String details = "Executed method: " + methodName;

            auditLogService.saveLog(username, action, details);
        } catch (Exception e) {
            System.err.println("Error creating audit log: " + e.getMessage());
        }
    }
}
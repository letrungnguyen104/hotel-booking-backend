package com.project.hotel.configuration;

import com.project.hotel.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

    private final AuditLogService auditLogService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUri = request.getRequestURI();

        if (requestUri.contains("/auth/token") || requestUri.contains("/auth/login")) {
            String username = event.getAuthentication().getName();
            auditLogService.saveLog(username, "LOGIN_SUCCESS", "User logged in successfully.");
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        String error = event.getException().getMessage();
        auditLogService.saveLog(username, "LOGIN_FAILED", "Failed login attempt. Reason: " + error);
    }
}
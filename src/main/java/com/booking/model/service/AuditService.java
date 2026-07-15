package com.booking.model.service;

import com.booking.model.dto.AuditLog;
import com.booking.model.dto.User;
import com.booking.repositories.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logAction(String action, String resourceType,
                          Long resourceId, String details, HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth != null ? auth.getName() : "anonymous";

            User user = User.builder()
                    .id(1L)
                    .email(email)
                    .build();

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .details(details)
                    .isAddress(getClientIp(request))
                    .build();

            auditLogRepository.save((auditLog));
            log.info("Audit: {} by {} for {}:{}", action, email, resourceType, resourceId);
        }catch (Exception e) {
            log.error("Failed to log audit: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return
                request.getRemoteAddr();
    }

}

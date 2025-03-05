package com.quickweather.admin;

import com.quickweather.service.admin.SecurityEventService;
import com.quickweather.service.user.UserLoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class SecurityEventListener implements ApplicationListener<ApplicationEvent> {

    private final SecurityEventService securityEventService;
    private final UserLoginAttemptService userLoginAttemptService;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        String ipAddress = getClientIpAddress();

        if (event instanceof AuthenticationSuccessEvent) {
            AuthenticationSuccessEvent successEvent = (AuthenticationSuccessEvent) event;
            String username = successEvent.getAuthentication().getName();
            userLoginAttemptService.resetFailedAttempts(username);
            securityEventService.logEvent(username, SecurityEventType.LOGIN_SUCCESS, ipAddress);
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            AbstractAuthenticationFailureEvent failureEvent = (AbstractAuthenticationFailureEvent) event;
            String username = failureEvent.getAuthentication().getName();
            userLoginAttemptService.incrementFailedAttempts(username);
            securityEventService.logEvent(username, SecurityEventType.LOGIN_FAILURE, ipAddress);
        }
    }

    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null && attributes.getRequest() != null) {
            return attributes.getRequest().getRemoteAddr();
        }
        return "unknown-ip";
    }
}


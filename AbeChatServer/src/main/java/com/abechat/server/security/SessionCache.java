package com.abechat.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class SessionCache {
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public SessionCache() {
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    }

    public void store(HttpServletRequest request, HttpServletResponse response, Authentication authenticationResult) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authenticationResult);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}

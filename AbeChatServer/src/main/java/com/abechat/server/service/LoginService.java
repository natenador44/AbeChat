package com.abechat.server.service;

import com.abechat.server.model.Request;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private final AuthenticationManager authenticationManager;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Autowired
    public LoginService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    }

    public void login(Request.Login loginRequest, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LOGGER.info("[{}] attempting login for user {}", request.getRemoteAddr(), loginRequest.username());
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
        try {
            Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
            populateSessionAuthentication(request, response, authenticationResult);
        } catch (AuthenticationException e) {
            LOGGER.error("[{}] Authentication failed for user {}", request.getRemoteAddr(), loginRequest.username());
            throw e;
        }
        LOGGER.info("[{}] Login successful for user {}", request.getRemoteAddr(), loginRequest.username());
    }

    private void populateSessionAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authenticationResult) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authenticationResult);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}

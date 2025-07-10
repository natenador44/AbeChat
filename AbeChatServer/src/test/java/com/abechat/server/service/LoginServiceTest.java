package com.abechat.server.service;

import com.abechat.server.TestUserSetup;
import com.abechat.server.model.Request;
import com.abechat.server.security.SessionCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private final HttpServletRequest REQUEST = mock(HttpServletRequest.class);
    private final HttpServletResponse RESPONSE = mock(HttpServletResponse.class);

    @Test
    void successfulLogin() {
        var sessionCache = mock(SessionCache.class);
        var authManager = mock(AuthenticationManager.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD);
        when(authManager.authenticate(any(Authentication.class))).thenReturn(authToken);

        var loginService = new LoginService(authManager, sessionCache);

        loginService.login(new Request.Login(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD), REQUEST, RESPONSE);

        verify(sessionCache, times(1)).store(REQUEST, RESPONSE, authToken);
    }

    @Test
    void unsuccessfulLogin() {
        var sessionCache = mock(SessionCache.class);
        var authManager = mock(AuthenticationManager.class);

        when(authManager.authenticate(any(Authentication.class))).thenThrow(BadCredentialsException.class);

        var loginService = new LoginService(authManager, sessionCache);

        assertThrows(AuthenticationException.class, () -> loginService.login(new Request.Login(TestUserSetup.GARY_USERNAME, TestUserSetup.GARY_PASSWORD), REQUEST, RESPONSE));
    }
}
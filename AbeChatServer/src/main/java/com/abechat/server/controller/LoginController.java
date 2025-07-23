package com.abechat.server.controller;

import com.abechat.server.model.Request;
import com.abechat.server.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/login")
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    //Rendering for login html page
    @GetMapping(path = {"", "/"})
    public String loginView() {
        return "login";
    }

    @PostMapping(path = {"", "/"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String login(Request.Login login, HttpServletRequest request, HttpServletResponse response) {
        try {
            loginService.login(login, request, response);
        } catch (AuthenticationException e) {
            return "redirect:/error?message=" + URLEncoder.encode("Error logging in user: " + e.getMessage(), StandardCharsets.UTF_8); //Sends specific error message to customer error page
        }
        return "redirect:/"; // change to whatever page we go to after login
    }
}

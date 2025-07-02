package com.abechat.server.api;

import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.Request;
import com.abechat.server.service.LoginService;
import com.abechat.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final LoginService loginService;
    private final UserService userService;

    @Autowired
    public UserController(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping(value = { "/login", "/login/" })
    public ResponseEntity<Void> login(Request.Login loginRequest, HttpServletRequest request, HttpServletResponse response) {
        loginService.login(loginRequest, request, response);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = { "/create", "/create/" })
    public ResponseEntity<Void> create(HttpServletRequest servletRequest, @RequestBody Request.NewUser newUserRequest) {
        try {
            LOGGER.info("[{}] Attempting to create new user {}", servletRequest.getRemoteAddr(), newUserRequest.username());
            userService.createNew(newUserRequest);
            LOGGER.info("[{}] Successfully created {}", servletRequest.getRemoteAddr(), newUserRequest.username());
            return ResponseEntity.created(URI.create("/abechat/login/")).build();
        } catch (UsernameAlreadyExists e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllUsernames() {
        return ResponseEntity.ok(userService.getAllUsernames());
    }
}

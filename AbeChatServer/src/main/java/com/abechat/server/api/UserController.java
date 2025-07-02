package com.abechat.server.api;

import com.abechat.server.exception.UsernameAlreadyExists;
import com.abechat.server.model.Request;
import com.abechat.server.security.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/user", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    }

    @PostMapping(value = { "/login", "/login/" })
    public ResponseEntity<Void> login(@RequestBody Request.Login loginRequest, HttpServletRequest request, HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());

        Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);

        // is there a way to NOT do this manually??
        populateSessionAuthentication(request, response, authenticationResult);

        return ResponseEntity.ok().build();
    }

    private void populateSessionAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authenticationResult) {
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authenticationResult);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);
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

package com.abechat.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.abechat.server.service.UserService;

@Controller
@RequestMapping("/createUser")
public class createUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(createUserController.class);
    private final UserService userService;

    public createUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = {"", "/"})
    public String createUserView() {
        return "createUser"; 
    }
}

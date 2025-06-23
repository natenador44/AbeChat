package com.abechat.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user/login")
public class LoginController {
    //Rendering for login html page
    @GetMapping(path = {"", "/"})
    public String login() {
        return "login";
    }
}

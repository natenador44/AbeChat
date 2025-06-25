package com.abechat.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/abechat")
public class HomeController {
    /*
    This is how you render html docs with thymeleaf. 
    This looks at resources/templates for a html file that matches the returned string.
    */
    @GetMapping(path = {"", "/"})
    public String index() {
        return "index";
    }
}

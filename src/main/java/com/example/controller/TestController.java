package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/")
    public String home () {
        return "home";
    }

    @GetMapping("/about")
    public String about () {
        return "about";
    }
}

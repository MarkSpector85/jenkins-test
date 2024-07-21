package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestRestController {

    @GetMapping("/users")
    public List<String> getUsers () {
       return List.of("Manas", "Mira");
    }

}

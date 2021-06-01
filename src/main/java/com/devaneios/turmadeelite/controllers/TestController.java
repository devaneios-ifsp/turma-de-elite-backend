package com.devaneios.turmadeelite.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/andre")
public class TestController {

    @GetMapping
    public String hello(Authentication authentication){
        return (String) authentication.getPrincipal();
    }
}


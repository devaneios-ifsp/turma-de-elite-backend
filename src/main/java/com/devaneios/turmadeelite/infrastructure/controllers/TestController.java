package com.devaneios.turmadeelite.infrastructure.controllers;

import com.devaneios.turmadeelite.infrastructure.security.FirebaseAuthenticationInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/andre")
public class TestController {

    @GetMapping
    public String hello(){
        return "authentication.getPrincipal()";
    }
}


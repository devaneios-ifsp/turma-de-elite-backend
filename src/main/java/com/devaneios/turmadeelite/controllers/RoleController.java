package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@AllArgsConstructor
public class RoleController {

    private final UserService userService;

    @GetMapping
    String getRole(Authentication authentication){
        return authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .get()
                .getAuthority();
    }
}

package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.AdminCreateDTO;
import com.devaneios.turmadeelite.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;

    @PostMapping
    ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminCreateDTO dto){
        this.userService.createAdminUser(dto.getEmail(), dto.getName(), dto.getLanguage());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

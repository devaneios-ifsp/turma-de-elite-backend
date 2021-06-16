package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.AdminCreateDTO;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
import com.devaneios.turmadeelite.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminCreateDTO dto){
        this.adminService.createAdminUser(dto.getEmail(), dto.getName(), dto.getLanguage());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

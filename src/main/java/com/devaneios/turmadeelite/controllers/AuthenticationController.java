package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.security.FirebaseAuthenticationInfo;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AdminFirstAccessService adminFirstAccessService;

    @PutMapping("/first-access/admin")
    ResponseEntity<?> doAdminFistAccess(
            @RequestBody FirstAccessDTO accessDTO,
            Authentication authentication){
        adminFirstAccessService.doFirstAccess(accessDTO, (String) authentication.getPrincipal());
        return ResponseEntity.ok().build();
    }

}

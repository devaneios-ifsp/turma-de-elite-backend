package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("first-access")
@AllArgsConstructor
public class FirstAccessController {

    private final AdminFirstAccessService service;

    @PostMapping("/verify-token")
    void verifyFirstAccessToken(@RequestBody String firstAccessToken){
        service.verifyToken(firstAccessToken);
    }

    @PostMapping
    void doFirstAccess(@RequestBody FirstAccessDTO firstAccessDTO) throws Exception {
        service.doFirstAccess(firstAccessDTO);
    }
}

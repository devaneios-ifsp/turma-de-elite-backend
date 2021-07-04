package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.devaneios.turmadeelite.services.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/managers")
@AllArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @Operation(summary = "Cadastrar um gestor e enviar instruções para realizar o primeiro acesso do mesmo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário gestor criado com sucesso. Enviada instruções de primeiro accesso para o e-mail"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já foi cadastrado"
            )
    })
    @PostMapping
    ResponseEntity<?> registerManagerUser(@RequestBody UserCredentialsCreateDTO userCredentialsCreateDTO){
        this.managerService.createManagerUser(
                userCredentialsCreateDTO.getEmail(),
                userCredentialsCreateDTO.getName(),
                userCredentialsCreateDTO.getLanguage());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

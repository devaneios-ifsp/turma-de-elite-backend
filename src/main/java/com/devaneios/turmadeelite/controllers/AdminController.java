package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.AdminCreateDTO;
import com.devaneios.turmadeelite.dto.AdminViewDTO;
import com.devaneios.turmadeelite.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Listar todos os administradores cadastrados no sistema, com todos os status")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista retornada com sucesso"
            )
    })
    @GetMapping
    ResponseEntity<Page<AdminViewDTO>> getPaginatedAdminUser(@RequestParam int size, @RequestParam int pageNumber){
        Page<AdminViewDTO> response = this.userService
                .getPaginatedAdminUsers(size, pageNumber)
                .map(AdminViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cadastrar um administrador e enviar instruções para realizar o primeiro acesso do mesmo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário admin criado com sucesso. Enviada requisição para o envio de e-mail"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já foi cadastrado"
            )
    })
    @PostMapping
    ResponseEntity<?> createAdminUser(@Valid @RequestBody AdminCreateDTO dto){
        this.userService.createAdminUser(dto.getEmail(), dto.getName(), dto.getIsActive(), dto.getLanguage());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

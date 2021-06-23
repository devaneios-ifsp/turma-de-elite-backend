package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("first-access")
@AllArgsConstructor
public class FirstAccessController {

    private final AdminFirstAccessService service;

    @Operation(summary = "Verificar token necessário para o primeiro acesso, enviado para o E-mail do usúario")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuário com o devido token encontrado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não foi encontrado um usuário com aquele token"
            ),
            @ApiResponse(
                    responseCode= "409",
                    description = "Usuário encontrado já realizou primeiro acesso"
            )
    })
    @PostMapping("/verify-token")
    ResponseEntity<String> verifyFirstAccessToken(@Valid @RequestBody String firstAccessToken){
        String email = service.verifyToken(firstAccessToken);
        return ResponseEntity.ok(email);
    }

    @Operation(summary = "Realizar primeiro acesso, cadastrando credenciais no serviço de autenticação externo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Credenciais cadastradas com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não foi encontrado um usuário com o token e/ou E-mail"
            ),
            @ApiResponse(
                    responseCode= "409",
                    description = "Usuário encontrado já realizou primeiro acesso"
            )
    })
    @PostMapping
    ResponseEntity<Object> doFirstAccess(@Valid @RequestBody FirstAccessDTO firstAccessDTO) throws Exception {
        service.doFirstAccess(firstAccessDTO);
        return ResponseEntity.status(201).build();
    }
}

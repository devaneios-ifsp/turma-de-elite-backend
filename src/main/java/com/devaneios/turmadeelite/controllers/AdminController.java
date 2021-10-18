package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.AdminViewDTO;
import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
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
import java.util.List;
import java.util.stream.Collectors;

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
    @IsAdmin
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
    @IsAdmin
    @PostMapping
    ResponseEntity<?> createAdminUser(@Valid @RequestBody UserCredentialsCreateDTO dto){
        this.userService.createAdminUser(dto.getEmail(), dto.getName(), dto.getIsActive(), dto.getLanguage());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Visualizar um administrador pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário Administrador encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não foi encontrado um usuário administrador com esse Id"
            )
    })
    @IsAdmin
    @GetMapping("/{userId}")
    @ResponseBody AdminViewDTO findAdminById(@PathVariable Long userId){
        UserCredentials adminById = this.userService.findAdminById(userId);
        return new AdminViewDTO(adminById);
    }

    @Operation(summary = "Visualizar uma lista de admins buscando pelo nome")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuários administradores encontrados com sucesso"
            ),
    })
    @IsAdmin
    @GetMapping("/name/{name}")
    ResponseEntity<List<AdminViewDTO>> getAdminsByNameSimilarity(@PathVariable String name){
        List<UserCredentials> paginatedAdmins = this.userService.getUsersByNameSimilarity(name);
        List<AdminViewDTO> response = paginatedAdmins.stream().map(AdminViewDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @IsAdmin
    @PutMapping("/{userId}")
    ResponseEntity<?> updateAdminUserInfo(@RequestBody UserCredentialsCreateDTO admin, @PathVariable Long userId){
        this.userService.updateAdminUser(userId,admin);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

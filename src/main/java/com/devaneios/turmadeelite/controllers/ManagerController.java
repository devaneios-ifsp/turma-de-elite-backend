package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.SchoolUserCreateDTO;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
import com.devaneios.turmadeelite.services.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @IsAdmin
    @PostMapping
    ResponseEntity<?> registerManagerUser(@RequestBody SchoolUserCreateDTO schoolUserCreateDTO){
        this.managerService.createManagerUser(
                schoolUserCreateDTO.getEmail(),
                schoolUserCreateDTO.getName(),
                schoolUserCreateDTO.getLanguage(),
                schoolUserCreateDTO.getSchoolId(),
                schoolUserCreateDTO.getIsActive());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Listar uma página de gestores")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de gestores retornada com sucesso"
            )
    })
    @IsAdmin
    @GetMapping
    ResponseEntity<Page<SchoolUserViewDTO>> getPaginatedManagers(@RequestParam int size, @RequestParam int pageNumber){
        Page<Manager> paginatedManagers = this.managerService.getPaginatedSchools(size, pageNumber);
        Page<SchoolUserViewDTO> response = paginatedManagers.map(SchoolUserViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Encontrar um gestor pelo seu id e a escola na qual está associado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gestor encontrado e retornado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Gestor com id especificado não encontrado"
            ),
    })
    @IsAdmin
    @GetMapping("/{id}")
    ResponseEntity<SchoolUserViewDTO> getManager(@PathVariable Long id){
        Manager manager = this.managerService.findManagerById(id);
        return ResponseEntity.ok(new SchoolUserViewDTO(manager));
    }

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
    @IsAdmin
    @PutMapping("/{id}")
    ResponseEntity<?> updateManager(@RequestBody SchoolUserCreateDTO schoolUserCreateDTO, @PathVariable Long id){
        this.managerService.updateManagerUser(
                schoolUserCreateDTO.getEmail(),
                schoolUserCreateDTO.getName(),
                schoolUserCreateDTO.getLanguage(),
                schoolUserCreateDTO.getSchoolId(),
                schoolUserCreateDTO.getIsActive(),
                id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

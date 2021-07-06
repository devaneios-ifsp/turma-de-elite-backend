package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.ManagerCreateDTO;
import com.devaneios.turmadeelite.dto.ManagerViewDTO;
import com.devaneios.turmadeelite.dto.SchoolViewDTO;
import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.School;
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
    @PostMapping
    ResponseEntity<?> registerManagerUser(@RequestBody ManagerCreateDTO managerCreateDTO){
        this.managerService.createManagerUser(
                managerCreateDTO.getEmail(),
                managerCreateDTO.getName(),
                managerCreateDTO.getLanguage(),
                managerCreateDTO.getSchoolId(),
                managerCreateDTO.getIsActive());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Listar uma página de gestores")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de gestores retornada com sucesso"
            )
    })
    @GetMapping
    ResponseEntity<Page<ManagerViewDTO>> getSchools(@RequestParam int size, @RequestParam int pageNumber){
        Page<Manager> paginatedManagers = this.managerService.getPaginatedSchools(size, pageNumber);
        Page<ManagerViewDTO> response = paginatedManagers.map(ManagerViewDTO::new);
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
    @GetMapping("/{id}")
    ResponseEntity<ManagerViewDTO> getManagers(@PathVariable Long id){
        Manager manager = this.managerService.findManagerById(id);
        return ResponseEntity.ok(new ManagerViewDTO(manager));
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
    @PutMapping("/{id}")
    ResponseEntity<?> updateManager(@RequestBody ManagerCreateDTO managerCreateDTO,@PathVariable Long id){
        this.managerService.updateManagerUser(
                managerCreateDTO.getEmail(),
                managerCreateDTO.getName(),
                managerCreateDTO.getLanguage(),
                managerCreateDTO.getSchoolId(),
                managerCreateDTO.getIsActive(),
                id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

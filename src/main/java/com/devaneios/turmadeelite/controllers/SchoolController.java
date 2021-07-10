package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
import com.devaneios.turmadeelite.services.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schools")
@AllArgsConstructor
public class SchoolController {
    private final SchoolService schoolService;

    @IsAdmin
    @Operation(summary = "Cadastra uma escola")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Escola cadastrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição com campos inválidos"
            ),
            @ApiResponse(
                    responseCode = "412",
                    description = "Já existe uma escola cadastrada com este identificador"
            )
    })
    @PostMapping
    ResponseEntity<?> createSchool(@RequestBody SchoolCreateDTO schoolCreateDTO){
        School school = schoolCreateDTO.toEntity();
        this.schoolService.createSchool(school);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Operation(summary = "Lista as escolas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna uma página de escolas"
            )
    })
    @IsAdmin
    @GetMapping
    ResponseEntity<Page<com.devaneios.turmadeelite.dto.SchoolViewDTO>> getSchools(@RequestParam int size, @RequestParam int pageNumber){
        Page<School> paginatedSchools = this.schoolService.getPaginatedSchools(size, pageNumber);
        Page<com.devaneios.turmadeelite.dto.SchoolViewDTO> response = paginatedSchools.map(com.devaneios.turmadeelite.dto.SchoolViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lista as escolas cadastradas por similaridade do nome")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna uma lista de escolas"
            )
    })
    @IsAdmin
    @GetMapping("/name/{name}")
    ResponseEntity<List<com.devaneios.turmadeelite.dto.SchoolViewDTO>> getSchoolsByNameSimilarity(@PathVariable String name){
        List<School> paginatedSchools = this.schoolService.getSchoolsByNameSimilarity(name);
        List<com.devaneios.turmadeelite.dto.SchoolViewDTO> response = paginatedSchools.stream().map(com.devaneios.turmadeelite.dto.SchoolViewDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Recuper uma escola pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna a escola"
            )
    })
    @IsAdmin
    @GetMapping("/{schoolId}")
    ResponseEntity<com.devaneios.turmadeelite.dto.SchoolViewDTO> getSchoolById(@PathVariable Long schoolId){
        School school = this.schoolService.getSchoolById(schoolId);
        return ResponseEntity.ok(new com.devaneios.turmadeelite.dto.SchoolViewDTO(school));
    }

    @Operation(summary = "Atualizar uma escola pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Escola atualizada com sucesso"
            )
    })
    @IsAdmin
    @PutMapping("/{schoolId}")
    ResponseEntity<?> updateSchoolById(@PathVariable Long schoolId,@RequestBody SchoolCreateDTO schoolCreateDTO){
        this.schoolService.updateSchoolById(schoolId,schoolCreateDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

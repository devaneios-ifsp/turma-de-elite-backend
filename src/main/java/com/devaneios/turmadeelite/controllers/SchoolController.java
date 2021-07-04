package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import com.devaneios.turmadeelite.dto.SchoolViewDTO;
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
    @ResponseStatus(code = HttpStatus.CREATED)
    void createSchool(@RequestBody SchoolCreateDTO schoolCreateDTO){
        School school = schoolCreateDTO.toEntity();
        this.schoolService.createSchool(school);
    }

    @Operation(summary = "Lista as escolas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna uma página de escolas"
            )
    })
    @GetMapping
    ResponseEntity<Page<SchoolViewDTO>> getSchools(@RequestParam int size, @RequestParam int pageNumber){
        Page<School> paginatedSchools = this.schoolService.getPaginatedSchools(size, pageNumber);
        Page<SchoolViewDTO> response = paginatedSchools.map(SchoolViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Recuper uma escola pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna a escola"
            )
    })
    @GetMapping("/{schoolId}")
    ResponseEntity<SchoolViewDTO> getSchoolById(@PathVariable Long schoolId){
        School school = this.schoolService.getSchoolById(schoolId);
        return ResponseEntity.ok(new SchoolViewDTO(school));
    }
}

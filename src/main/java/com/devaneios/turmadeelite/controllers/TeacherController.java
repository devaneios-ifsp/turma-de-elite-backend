package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.dto.SchoolUserCreateDTO;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.security.guards.IsManager;

import com.devaneios.turmadeelite.services.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@AllArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;

    @Operation(summary = "Cadastrar um professor e enviar instruções para realizar o primeiro acesso do mesmo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário professor criado com sucesso. Enviada instruções de primeiro accesso para o e-mail"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já foi cadastrado"
            )
    })
    @IsManager
    @PostMapping
    ResponseEntity<?> registerTeacher(@RequestBody SchoolUserCreateDTO schoolUserCreateDTO){
        this.teacherService.createTeacherUser(
                schoolUserCreateDTO.getEmail(),
                schoolUserCreateDTO.getName(),
                schoolUserCreateDTO.getLanguage(),
                schoolUserCreateDTO.getSchoolId(),
                schoolUserCreateDTO.getIsActive());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Listar uma página de professores")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de professores retornada com sucesso"
            )
    })
    @IsManager
    @GetMapping
    ResponseEntity<Page<SchoolUserViewDTO>> getTeachers(@RequestParam int size, @RequestParam int pageNumber){
        Page<Teacher> paginatedTeachers = this.teacherService.getPaginatedTeachers(size, pageNumber);
        Page<SchoolUserViewDTO> response = paginatedTeachers.map(SchoolUserViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cadastrar um professor e enviar instruções para realizar o primeiro acesso do mesmo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário professor criado com sucesso. Enviada instruções de primeiro accesso para o e-mail"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já foi cadastrado"
            )
    })
    @IsManager
    @PutMapping("/{id}")
    ResponseEntity<?> updateTeacher(@RequestBody SchoolUserCreateDTO teacherCreateDTO, @PathVariable Long id){
        this.teacherService.updateTeacherUser(
                teacherCreateDTO.getEmail(),
                teacherCreateDTO.getName(),
                teacherCreateDTO.getLanguage(),
                teacherCreateDTO.getSchoolId(),
                teacherCreateDTO.getIsActive(),
                id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Encontrar um professor pelo seu id e a escola na qual está associado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Professor encontrado e retornado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Professor com id especificado não encontrado"
            ),
    })
    @IsManager
    @GetMapping("/{id}")
    ResponseEntity<SchoolUserViewDTO> getTeachers(@PathVariable Long id){
        Teacher teacher = this.teacherService.findTeacherById(id);
        return ResponseEntity.ok(new SchoolUserViewDTO(teacher));
    }
}

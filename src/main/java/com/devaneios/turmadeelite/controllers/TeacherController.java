package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.*;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
import com.devaneios.turmadeelite.security.guards.IsManager;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import com.devaneios.turmadeelite.services.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @PostMapping
    ResponseEntity<?> registerTeacher(@RequestBody TeacherCreateDTO teacherCreateDTO, Authentication authentication){
        this.teacherService.createTeacherUser(
                teacherCreateDTO.getEmail(),
                teacherCreateDTO.getName(),
                teacherCreateDTO.getLanguage(),
                teacherCreateDTO.getIsActive(),
                (String) authentication.getPrincipal());
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
    ResponseEntity<Page<SchoolUserViewDTO>> getTeacherById(
            @RequestParam int size,
            @RequestParam int pageNumber,
            Authentication authentication
    ){
        Page<Teacher> paginatedTeachers = this.teacherService.getPaginatedTeachers(
                                                size,
                                                pageNumber,
                                                (String) authentication.getPrincipal()
                                            );
        Page<SchoolUserViewDTO> response = paginatedTeachers.map(SchoolUserViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Visualizar uma lista de professores buscando pelo nome")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Professores encontrados com sucesso"
            ),
    })
    @GetMapping("/name/{name}")
    ResponseEntity<List<SchoolUserViewDTO>> getTeachersByNameSimilarity(@PathVariable String name){
        List<Teacher> paginatedTeacher = this.teacherService.getTeachersByNameSimilarity(name).orElse(new ArrayList<>());
        List<SchoolUserViewDTO> response = paginatedTeacher.stream().map(SchoolUserViewDTO::new).collect(Collectors.toList());
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
    ResponseEntity<?> updateTeacher(
            @RequestBody TeacherCreateDTO teacherCreateDTO,
            @PathVariable Long id,
            Authentication authentication
    ){
        this.teacherService.updateTeacherUser(
                teacherCreateDTO.getEmail(),
                teacherCreateDTO.getName(),
                teacherCreateDTO.getLanguage(),
                teacherCreateDTO.getIsActive(),
                id,
                (String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.OK);
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
    ResponseEntity<SchoolUserViewDTO> getTeacherById(@PathVariable Long id,Authentication authentication){
        Teacher teacher = this.teacherService.findTeacherById(id,(String) authentication.getPrincipal());
        return ResponseEntity.ok(new SchoolUserViewDTO(teacher));
    }

    @Operation(summary = "Encontrar professores da escola através do email ou de parte do email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Professores encontrados e retornados com sucesso"
            ),
    })
    @IsManager
    @GetMapping("/email/{email}")
    @ResponseBody List<SchoolUserViewDTO> getTeachersByEmail(@PathVariable String email, Authentication authentication){
        return this.teacherService
                .findTeachersByEmailSubstring(
                        email,
                        (String) authentication.getPrincipal())
                .stream()
                .map(SchoolUserViewDTO::new)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Visualizar uma lista de atividades postadas e entregues por turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividades postadas e entregues encontradas com sucesso"
            ),
    })
    @IsTeacher
    @GetMapping("/dash")
    ResponseEntity<List<ActivityPostDeliveryDTO>> getPostDeliveryActivities(Authentication authentication){
        return ResponseEntity.ok(this.teacherService.getPostDeliveryActivities((String) authentication.getPrincipal()));
    }

    @Operation(summary = "Visualizar uma lista de alunos ordenada por pontuação")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pontuações de cada aluno encontradas com sucesso"
            ),
    })
    @IsTeacher
    @GetMapping("/punctuations")
    ResponseEntity<List<StudentPunctuationDTO>> getStudentPunctuations(Authentication authentication){
        return ResponseEntity.ok(this.teacherService.getStudentPunctuations((String) authentication.getPrincipal()));
    }

    @Operation(summary = "Listar a quantidade de atividades postadas por professor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista das atividades postadas por professor retornada com sucesso"
            )
    })
    @IsManager
    @GetMapping("activities-by-teacher")
    ResponseEntity<List<ActivityByTeacherDTO>> getActivitiesByTeacher(Authentication authentication) throws IOException {
        return ResponseEntity.ok(this.teacherService.getActivitiesByTeacher((String)authentication.getPrincipal()));
    }
}

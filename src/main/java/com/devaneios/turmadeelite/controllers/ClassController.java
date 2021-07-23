package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.*;
import com.devaneios.turmadeelite.entities.SchoolClass;
import com.devaneios.turmadeelite.security.guards.IsManager;
import com.devaneios.turmadeelite.services.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/class")
@AllArgsConstructor
public class ClassController {

    private final ClassService classService;

    @Operation(summary = "Cadastrar uma turma enviando professores e alunos")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Turma criada com sucesso"
            )
    })
    @IsManager
    @PostMapping
    ResponseEntity<?> createClass(@Valid @RequestBody ClassCreateDTO dto, Authentication authentication){
        this.classService.createClass(dto, (String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Adiciona um professor a determinada turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Professor adicionado com sucesso"
            )
    })
    @IsManager
    @PostMapping("/{classId}/teacher/{teacherId}")
    ResponseEntity<?> addTeacherToClass(@PathVariable Long classId, @PathVariable Long teacherId, Authentication authentication){
        this.classService.addTeacherToClass((String) authentication.getPrincipal(),classId,teacherId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Recuperar uma turma pelo seu id requisitado pelo gestor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turma encontrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não foi encontrado uma turma com este id na escola do cliente"
            )
    })
    @IsManager
    @GetMapping("/{id}")
    ResponseEntity<?> getSchoolClassById(@PathVariable Long id,Authentication authentication){
        SchoolClass schoolClass = this.classService.getSchoolClassByIdAsManager(id, (String) authentication.getPrincipal());
        return ResponseEntity.ok(new SchoolClassViewDTO(schoolClass));
    }

    @Operation(summary = "Recuperar uma turma pelo seu id, requisitado pelo professor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turma encontrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não foi encontrado uma turma com este id na escola do cliente"
            )
    })
    @IsManager
    @GetMapping("/{id}/general-info")
    ResponseEntity<?> getSchoolClassByIdGeneralInfo(@PathVariable Long id,Authentication authentication){
        SchoolClass schoolClass = this.classService.getSchoolClassByIdAsTeacher(id, (String) authentication.getPrincipal());
        return ResponseEntity.ok(new SchoolClassViewDTO(schoolClass));
    }

    @Operation(summary = "Encerra uma turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turma encerrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Não foi encontrado uma turma com este id na escola do cliente"
            )
    })
    @IsManager
    @PutMapping("/{id}/close")
    ResponseEntity<?> closeClass(@PathVariable Long id,Authentication authentication){
        this.classService.closeClass(id, (String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Recuperar todas as turmas do cliente")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turmas encontradas com sucesso"
            )
    })
    @IsManager
    @GetMapping
    ResponseEntity<Page<SchoolClassViewDTO>> getAllClassesBySchool(
            @RequestParam int size,
            @RequestParam int pageNumber,
            Authentication authentication){
        Page<SchoolClass> schoolClassPage = this.classService.getAllClasses(
                (String) authentication.getPrincipal(),pageNumber,size);
        Page<SchoolClassViewDTO> viewPage = schoolClassPage.map(SchoolClassViewDTO::new);
        return ResponseEntity.ok(viewPage);
    }

    @Operation(summary = "Recuperar todas as turmas do professor que requisitar")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turmas encontradas com sucesso"
            )
    })
    @IsManager
    @GetMapping("/teacher-himself")
    @ResponseBody Object getAllClassesBySchool(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer pageNumber,
            Authentication authentication
    ){
        if(size != null){
            return this.classService
                    .getAllClassesOfTeacher((String) authentication.getPrincipal(),pageNumber,size)
                    .map(SchoolClassNameDTO::new);
        } else {
            return this.classService
                    .getAllClassesOfTeacher((String) authentication.getPrincipal())
                    .stream()
                    .map(SchoolClassNameDTO::new)
                    .collect(Collectors.toList());
        }

    }

    @Operation(summary = "Atualizar nome e status de uma turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Turma atualizada com sucesso"
            )
    })
    @IsManager
    @PutMapping("/{id}")
    ResponseEntity<?> updateStatusAndName(
            @RequestBody ClassStatusNameDTO statusNameDTO,
            @PathVariable Long id,
            Authentication authentication){
        this.classService.updateStatusAndName(id, (String) authentication.getPrincipal(),statusNameDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Mudar status de um estudante em determinada turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status do estudante atualizado com sucesso"
            )
    })
    @IsManager
    @PutMapping("/{classId}/student/{studentId}")
    ResponseEntity<?> updateStudentStatusInClass(
            @RequestBody Boolean status,
            @PathVariable Long classId,
            @PathVariable Long studentId,
            Authentication authentication){
        this.classService.updateStudentStatus(classId,studentId,status,(String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Mudar status de um professor em determinada turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status do professor atualizado com sucesso"
            )
    })
    @IsManager
    @PutMapping("/{classId}/teacher/{teacherId}")
    ResponseEntity<?> updateTeacherStatusInClass(
            @RequestBody Boolean status,
            @PathVariable Long classId,
            @PathVariable Long teacherId,
            Authentication authentication){
        this.classService.updateTeacherStatus(classId,teacherId,status,(String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

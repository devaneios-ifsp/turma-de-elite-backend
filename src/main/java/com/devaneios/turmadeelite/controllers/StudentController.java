package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.StudentCreateDTO;
import com.devaneios.turmadeelite.dto.StudentViewDTO;
import com.devaneios.turmadeelite.entities.Student;
import com.devaneios.turmadeelite.security.guards.IsManager;
import com.devaneios.turmadeelite.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "Cadastrar um aluno e enviar instruções para realizar o primeiro acesso do mesmo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário aluno criado com sucesso. Enviada instruções de primeiro accesso para o e-mail"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já foi cadastrado"
            )
    })
    @PostMapping
    ResponseEntity<?> registerStudent(@RequestBody StudentCreateDTO studentDTO, Authentication authentication){
        this.studentService.createStudent(studentDTO,(String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Listar uma página de alunos")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de alunos retornada com sucesso"
            )
    })
    @IsManager
    @GetMapping
    ResponseEntity<Page<StudentViewDTO>> getStudentPaginated(
            @RequestParam int size,
            @RequestParam int pageNumber,
            Authentication authentication
    ){
        Page<Student> paginatedTeachers = this.studentService.getPaginatedStudents(
                size,
                pageNumber,
                (String) authentication.getPrincipal()
        );
        Page<StudentViewDTO> response = paginatedTeachers.map(StudentViewDTO::new);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cadastrar um aluno e enviar instruções para realizar o primeiro acesso do mesmo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário aluno criado com sucesso. Enviada instruções de primeiro accesso para o e-mail"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já foi cadastrado"
            )
    })
    @IsManager
    @PutMapping("/{id}")
    ResponseEntity<?> updateStudent(
            @RequestBody StudentCreateDTO studentCreateDTO,
            @PathVariable Long id,
            Authentication authentication
    ){
        this.studentService.updateStudent(
                studentCreateDTO,
                id,
                (String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Encontrar um aluno pelo seu id e a escola na qual está associado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Aluno encontrado e retornado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Aluno com id especificado não encontrado"
            ),
    })
    @IsManager
    @GetMapping("/{id}")
    ResponseEntity<StudentViewDTO> getStudentById(@PathVariable Long id,Authentication authentication){
        Student student = this.studentService.findStudentById(id,(String) authentication.getPrincipal());
        return ResponseEntity.ok(new StudentViewDTO(student));
    }

}

package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.*;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.security.guards.IsStudent;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import com.devaneios.turmadeelite.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "Cria uma atividade a ser entregue ou não")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Atividade criada com sucesso"
            )
    })
    @IsTeacher
    @PostMapping
    ResponseEntity<?> createActivity(@ModelAttribute ActivityCreateDTO activityCreateDTO, Authentication authentication) throws IOException, NoSuchAlgorithmException {
        this.activityService.createActivity(activityCreateDTO,(String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Atualiza uma atividade criada")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade atualizada com sucesso"
            )
    })
    @IsTeacher
    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(
            @PathVariable Long id,
            @ModelAttribute ActivityCreateDTO activityCreateDTO,
            Authentication authentication) throws IOException, NoSuchAlgorithmException {
        this.activityService.updateActivity(activityCreateDTO,(String) authentication.getPrincipal(),id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Lista todas as atividades criadas pelo professor")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividades listadas com sucesso"
            )
    })
    @IsTeacher
    @GetMapping
    public @ResponseBody Object getAllActivitiesOfTeacher(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer pageNumber,
            Authentication authentication){
        if(pageSize == null){
            return this.activityService.getAllActivitiesOfTeacher((String) authentication.getPrincipal())
                    .stream()
                    .map(ActivityViewNameDTO::new)
                    .collect(Collectors.toList());
        }else{
            return this.activityService
                    .getAllActivitiesOfTeacherPaginated((String) authentication.getPrincipal(),pageSize,pageNumber)
                    .map(ActivityViewDTO::new);
        }
    }

    @Operation(summary = "Recupera uma atividade pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade encontrada com sucesso"
            )
    })
    @IsTeacher
    @GetMapping("/{id}")
    public @ResponseBody ActivityViewDTO getAllActivitiesOfTeacher(
            @PathVariable Long id,
            Authentication authentication){

        Activity activity = this.activityService
                .getActivityByIdAndTeacher(id, (String) authentication.getPrincipal());
        return new ActivityViewDTO(activity);
    }

    @Operation(summary = "Professor realiza o download do anexo de uma atividade pelo id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade encontrada com sucesso"
            )
    })
    @IsTeacher
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadTeacherActivityAttachment(
            @PathVariable Long id,
            Authentication authentication
    ) throws IOException {

        AttachmentDTO attachmentDTO = this.activityService.getTeacherAttachmentFromActivity(id, (String) authentication.getPrincipal());

        InputStreamResource resource = new InputStreamResource(attachmentDTO.inputStream);
        return ResponseEntity.ok()
                .header("filename", attachmentDTO.filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Realiza o download do anexo de uma atividade pelo id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade encontrada com sucesso"
            )
    })
    @IsStudent
    @GetMapping("/{id}/student/download")
    public ResponseEntity<Resource> downloadStudentActivityAttachmentId(
            @PathVariable Long id,
            Authentication authentication
    ) throws IOException {

        AttachmentDTO attachmentDTO = this.activityService.getStudentAttachmentFromActivity(id, (String) authentication.getPrincipal());

        InputStreamResource resource = new InputStreamResource(attachmentDTO.inputStream);
        return ResponseEntity.ok()
                .header("filename", attachmentDTO.filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Lista todas as atividades que podem ser visualizadas pelo aluno")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividades encontrada com sucesso"
            )
    })
    @IsStudent
    @GetMapping("/student")
    public @ResponseBody List<StudentActivitiesDTO> getStudentActivities(Authentication authentication) throws IOException {
        return this.activityService.getStudentActivities((String) authentication.getPrincipal());
    }

    @Operation(summary = "Busca os detalhes de uma atividade, utilizando o Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Atividade encontrada com sucesso"
            )
    })
    @IsStudent
    @GetMapping("/{activityId}/class/{classId}/student")
    public @ResponseBody StudentActivityDetailsDTO getActivityDetails(
            @PathVariable Long activityId,
            @PathVariable Long classId,
            Authentication authentication
    ) throws IOException {
        return this.activityService.getActivityDetailsById((String) authentication.getPrincipal(), activityId,classId);
    }


}

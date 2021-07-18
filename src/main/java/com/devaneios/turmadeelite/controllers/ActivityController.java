package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.devaneios.turmadeelite.dto.ActivityViewDTO;
import com.devaneios.turmadeelite.dto.AdminViewDTO;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import com.devaneios.turmadeelite.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    ResponseEntity<?> getPaginatedAdminUser(@RequestBody ActivityCreateDTO activityCreateDTO, Authentication authentication){
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
            @RequestBody ActivityCreateDTO activityCreateDTO,
            Authentication authentication){
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
    public @ResponseBody Page<ActivityViewDTO> getAllActivitiesOfTeacher(
            @RequestParam int pageSize,
            @RequestParam int pageNumber,
            Authentication authentication){

        return this.activityService
                .getAllActivitiesOfTeacher((String) authentication.getPrincipal(),pageSize,pageNumber)
                .map(ActivityViewDTO::new);
    }
}

package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.AchievementCreateDTO;
import com.devaneios.turmadeelite.dto.AchievementPanelDTO;
import com.devaneios.turmadeelite.dto.AchievementViewDTO;
import com.devaneios.turmadeelite.entities.Achievement;
import com.devaneios.turmadeelite.security.guards.IsAdmin;
import com.devaneios.turmadeelite.security.guards.IsStudent;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import com.devaneios.turmadeelite.services.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
@AllArgsConstructor
public class AchievementsController {

    private final AchievementService achievementService;

    @IsTeacher
    @Operation(summary = "Cadastra uma conquista para uma determinada turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Conquista cadastrada com sucesso"
            )
    })
    @PostMapping
    ResponseEntity<?> createAchievement(@RequestBody AchievementCreateDTO achievementCreateDTO, Authentication authentication){
        this.achievementService.createAchievement(achievementCreateDTO,(String) authentication.getPrincipal());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Operation(summary = "Lista as conquistas cadastradas para as turmas que o professor dá aula")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna uma página de conquistas"
            )
    })
    @IsTeacher
    @GetMapping
    @ResponseBody Page<AchievementViewDTO> getAchievements(@RequestParam int size, @RequestParam int pageNumber, Authentication authentication){
        Page<Achievement> achievements = this.achievementService.getAchievements(size,pageNumber,(String) authentication.getPrincipal());
        return achievements.map(AchievementViewDTO::new);
    }

    @Operation(summary = "Recuper uma conquista pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorna a conquista"
            )
    })
    @IsTeacher
    @GetMapping("/{achievementId}")
    @ResponseBody AchievementViewDTO getAchievementById(@PathVariable Long achievementId){
        return this.achievementService
                .findAchievementById(achievementId)
                .map(AchievementViewDTO::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Atualiza uma conquista pelo Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conquista atualizada com sucesso"
            )
    })
    @IsAdmin
    @PutMapping("/{achievementId}")
    ResponseEntity<?> updateAchievementById(@PathVariable Long achievementId,@RequestBody AchievementCreateDTO achievementCreateDTO,Authentication authentication){
        this.achievementService.updateAchievement(achievementId,achievementCreateDTO,(String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Recupera as conquistas de um aluno")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conquistas encontradas com sucesso"
            )
    })
    @IsStudent
    @GetMapping("/student")
    @ResponseBody
    List<AchievementPanelDTO> getAchievementsAcquiredByStudent(Authentication authentication){
        return this.achievementService
                .getStudentAchievements((String) authentication.getPrincipal());
    }


}

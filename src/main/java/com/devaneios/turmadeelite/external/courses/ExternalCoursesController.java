package com.devaneios.turmadeelite.external.courses;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.TierConfigDTO;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.external.ExternalDeliverAchievements;
import com.devaneios.turmadeelite.security.guards.IsManager;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/courses")
@AllArgsConstructor
public class ExternalCoursesController {

    private final ExternalCoursesService externalCoursesService;
    private final ExternalDeliverAchievements externalDeliverAchievements;

    @IsManager
    @Operation(summary = "Lista todos os cursos de um sistema externo")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cursos encontrados com sucesso"
            )
    })
    @GetMapping
    public List<SchoolClassViewDTO> listAllCourses(Authentication authentication) throws IOException {
            String authUuid = (String) authentication.getPrincipal();
            return this.externalCoursesService.getAllCourses(authUuid);
    }

    @GetMapping("{courseId}")
    public SchoolClassViewDTO getExternalCourseById(@PathVariable String courseId, Authentication authentication) throws IOException {
        String authUuid = (String) authentication.getPrincipal();
        return this.externalCoursesService.findCourseById(courseId,authUuid);
    }

    @GetMapping("{courseId}/close-class")
    public ResponseEntity<?> closeExternalClass(@PathVariable String courseId,Authentication authentication) throws IOException {
        String authUuid = (String) authentication.getPrincipal();
        this.externalDeliverAchievements.deliverAchievementsToStudentsInCourse(authUuid,courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @IsTeacher
    @GetMapping("/authenticated-teacher")
    public List<SchoolClassViewDTO> listClassesFromTeacher(Authentication authentication) throws IOException {
        String authUuid = (String) authentication.getPrincipal();
        return this.externalCoursesService.getCoursesFromTeacher(authUuid);
    }

    @Operation(summary = "Salvar as configurações de tier para uma turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status do professor atualizado com sucesso"
            )
    })
    @IsTeacher
    @PostMapping("/{classId}/tier-config")
    ResponseEntity<Void> createClassTierConfiguration(
            @Valid @RequestBody TierConfigDTO tierConfigDTO,
            @PathVariable String classId
    ){
        this.externalCoursesService.saveTierConfig(tierConfigDTO,classId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

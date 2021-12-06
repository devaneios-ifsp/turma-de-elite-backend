package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.SchoolClassNameDTO;
import com.devaneios.turmadeelite.dto.StudentRankingDTO;
import com.devaneios.turmadeelite.security.guards.IsStudent;
import com.devaneios.turmadeelite.services.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@AllArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @Operation(summary = "Recupera uma página de turmas ranqueadas (que foram encerradas e os alunos podem ser ordenados)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de turmas encontrada"
            )
    })
    @IsStudent
    @GetMapping("/classes")
    public @ResponseBody Page<SchoolClassNameDTO> getRankedClasses(
            @RequestParam Integer size,
            @RequestParam Integer pageNumber,
            Authentication authentication){
        return this.rankingService
                .getRankeableClassesList((String) authentication.getPrincipal(),size,pageNumber)
                .map(SchoolClassNameDTO::new);
    }

    @Operation(summary = "Recupera o próprio ranking e o top três de determinada turma")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ranking encontrado com sucesso"
            )
    })
    @IsStudent
    @GetMapping("/classes/{classId}")
    public @ResponseBody List<StudentRankingDTO> getTopAndSelfRanking(Authentication authentication, @PathVariable Long classId){
        return this.rankingService
                .getRankingByClass((String) authentication.getPrincipal(),classId);
    }
}

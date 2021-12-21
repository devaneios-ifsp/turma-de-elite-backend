package com.devaneios.turmadeelite.external.ranking;


import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.StudentRankingDTO;
import com.devaneios.turmadeelite.security.guards.IsStudent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/external/ranking")
@AllArgsConstructor
public class ExternalRankingController {

    private final ExternalRankingService rankingService;

    @Operation(summary = "Recupera turmas ranqueadas (que foram encerradas e os alunos podem ser ordenados)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de turmas encontrada"
            )
    })
    @IsStudent
    @GetMapping("/classes")
    public List<SchoolClassViewDTO> getRankedClasses(Authentication authentication) throws IOException {
        String authUuid = (String) authentication.getPrincipal();
        return this.rankingService.getRankeableClasses(authUuid);
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
    public @ResponseBody
    List<StudentRankingDTO> getTopAndSelfRanking(Authentication authentication, @PathVariable String classId){
        return this.rankingService
                .getRankingByClass((String) authentication.getPrincipal(),classId);
    }
}

package com.devaneios.turmadeelite.controllers;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.devaneios.turmadeelite.dto.ActivityDeliveriesDTO;
import com.devaneios.turmadeelite.dto.AttachmentDTO;
import com.devaneios.turmadeelite.security.guards.IsStudent;
import com.devaneios.turmadeelite.security.guards.IsTeacher;
import com.devaneios.turmadeelite.services.ActivityDeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/activity-deliveries")
@AllArgsConstructor
public class ActivityDeliveryController {

    private final ActivityDeliveryService deliveryService;

    @Operation(summary = "Realiza a entrega de uma atividade")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Atividade entregue com sucesso"
            ),
            @ApiResponse(
                    responseCode = "412",
                    description = "Atividade já entregue ou expirada"
            )
    })
    @IsStudent
    @PostMapping("/activity/{activityId}")
    ResponseEntity<?> deliveryActivity(
            @RequestPart("document") MultipartFile deliveryDocument,
            @PathVariable Long activityId,
            Authentication authentication) throws IOException, NoSuchAlgorithmException {
        System.out.println(deliveryDocument.getName());
        this.deliveryService.deliveryActivity(deliveryDocument,activityId,(String) authentication.getPrincipal());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Realiza a correção de uma atividade")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Atividade entregue com sucesso"
            ),
            @ApiResponse(
                    responseCode = "412",
                    description = "Atividade já entregue ou expirada"
            )
    })
    @IsStudent
    @PostMapping("/{deliveryId}/grade")
    ResponseEntity<?> getPaginatedAdminUser(
            @PathVariable Long deliveryId,
            @RequestBody Float gradePercentage,
            Authentication authentication) throws IOException, NoSuchAlgorithmException {
        this.deliveryService.giveGradeToDelivery(deliveryId,(String) authentication.getPrincipal(),gradePercentage);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Realiza o download da entrega de uma atividade pelo id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrega encontrada com sucesso"
            )
    })
    @IsStudent
    @GetMapping("/student/activity/{activityId}/download")
    public ResponseEntity<Resource> downloadStudentActivityAttachmentId(
            @PathVariable Long activityId,
            Authentication authentication
    ) throws IOException {

        AttachmentDTO attachmentDTO = this.deliveryService.getStudentDeliveryAttachment(activityId, (String) authentication.getPrincipal());

        InputStreamResource resource = new InputStreamResource(attachmentDTO.inputStream);
        return ResponseEntity.ok()
                .header("filename", attachmentDTO.filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Realiza o download da entrega de uma atividade pelo id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrega encontrada com sucesso"
            )
    })
    @IsTeacher
    @GetMapping("/{activityDeliveryId}/download")
    public ResponseEntity<Resource> downloadTeacherActivityAttachmentId(
            @PathVariable Long activityDeliveryId
    ) throws IOException {

        AttachmentDTO attachmentDTO = this.deliveryService.getDeliveryAttachment(activityDeliveryId);

        InputStreamResource resource = new InputStreamResource(attachmentDTO.inputStream);
        return ResponseEntity.ok()
                .header("filename", attachmentDTO.filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "Realiza o download da entrega de uma atividade pelo id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Entrega encontrada com sucesso"
            )
    })
    @IsTeacher
    @GetMapping("/teacher/activity/{activityId}")
    public @ResponseBody
    List<ActivityDeliveriesDTO> getDeliveriesByActivityId(
            @PathVariable Long activityId,
            Authentication authentication
    ) throws IOException {
            return this.deliveryService.getDeliveriesByActivity(activityId,(String) authentication.getPrincipal());
    }
}

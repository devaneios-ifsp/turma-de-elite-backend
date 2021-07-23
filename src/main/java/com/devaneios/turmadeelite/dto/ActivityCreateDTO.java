package com.devaneios.turmadeelite.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Size(min = 1)
    private List<Long> schoolClasses;
    @Min(0L)
    private Double punctuation;
    @NotNull
    private Boolean isVisible;
    @NotNull
    private Boolean isActive;

    private String maxDeliveryDate;


    public LocalDateTime getFormattedDeliveryDate(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(this.getMaxDeliveryDate(), formatter);
    }
}

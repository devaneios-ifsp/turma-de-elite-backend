package com.devaneios.turmadeelite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClassCreateDTO {
    @Size(min=1)
    private List<Long> teachersId;
    @Size(min=1)
    private List<Long> studentsId;
    @NotBlank
    private String className;
    @NotNull
    private Boolean isActive;
}

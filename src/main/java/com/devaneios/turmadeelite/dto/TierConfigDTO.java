package com.devaneios.turmadeelite.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TierConfigDTO {

    @NotNull
    @Min(2)
    @Max(98)
    private Float goldPercent;

    @NotNull
    @Min(2)
    @Max(98)
    private Float silverPercent;

    @NotNull
    @Min(2)
    @Max(98)
    private Float bronzePercent;

}

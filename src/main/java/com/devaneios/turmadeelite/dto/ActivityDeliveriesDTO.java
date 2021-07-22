package com.devaneios.turmadeelite.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDeliveriesDTO {
    public Long deliveryId;
    public Long studentId;
    public String studentName;
    public String filename;
    public Float percentageReceived;
}

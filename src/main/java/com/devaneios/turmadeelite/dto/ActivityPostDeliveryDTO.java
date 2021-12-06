package com.devaneios.turmadeelite.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ActivityPostDeliveryDTO {
    private String className;
    private int postActivity;
    private int deliveryActivity;
}

package com.devaneios.turmadeelite.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserActiveInactiveDTO {
    private int month;
    private int year;
    private int activeUser;
    private int inactiveUser;

}

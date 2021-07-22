package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Activity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityViewNameDTO {
    public Long id;
    private String name;

    public ActivityViewNameDTO(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
    }
}

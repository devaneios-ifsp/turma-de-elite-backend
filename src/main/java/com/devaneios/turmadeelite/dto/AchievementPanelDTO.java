package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Achievement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementPanelDTO {
    public String name;
    public String description;
    public String iconName;
    public Boolean isAcquired;

    public AchievementPanelDTO(Achievement achievement, boolean contains) {
        this.name = achievement.getName();
        this.description = achievement.getDescription();
        this.iconName = achievement.getIconName();
        this.isAcquired = contains;
    }
}

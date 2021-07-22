package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Achievement;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementViewDTO {
    public Long id;
    public String name;
    public String description;
    public String iconName;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beforeAt;

    private Integer earlierOf;

    private Integer bestOf;

    private Double averageGradeGreaterOrEqualsThan;

    private Boolean isActive;

    private Long classId;

    private Long activityId;

    public AchievementViewDTO(Achievement achievement) {
        this.id = achievement.getId();
        this.name = achievement.getName();
        this.description = achievement.getDescription();
        this.iconName = achievement.getIconName();
        this.beforeAt = achievement.getBeforeAt();
        this.earlierOf = achievement.getEarlierOf();
        this.bestOf = achievement.getBestOf();
        this.averageGradeGreaterOrEqualsThan = achievement.getAverageGradeGreaterOrEqualsThan();
        this.isActive = achievement.getIsActive();
        this.classId = achievement.getSchoolClass().getId();
        this.activityId = achievement.getActivity().getId();
    }
}

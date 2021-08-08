package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Achievement;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.SchoolClass;
import com.devaneios.turmadeelite.entities.Teacher;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AchievementCreateDTO {
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

    public Achievement toEntity(Teacher teacher){
        Achievement.AchievementBuilder builder = Achievement
                .builder()
                .name(this.name)
                .description(this.description)
                .iconName(this.iconName)
                .beforeAt(this.beforeAt)
                .earlierOf(this.earlierOf)
                .bestOf(this.bestOf)
                .averageGradeGreaterOrEqualsThan(this.averageGradeGreaterOrEqualsThan)
                .isActive(this.isActive)
                .teacher(teacher);

        if(this.classId != null){
            builder.schoolClass(SchoolClass.builder().id(this.classId).build());
        }
        if(this.activityId != null){
            builder.activity(Activity.builder().id(this.activityId).build());
        }
        return builder.build();
    }
}

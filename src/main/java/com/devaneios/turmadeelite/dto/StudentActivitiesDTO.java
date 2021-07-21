package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.ActivityStatus;
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
public class StudentActivitiesDTO {
    public Long id;
    public String name;
    public Double maxPunctuation;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public LocalDateTime expireDate;
    public String teacherName;

    public Boolean isRevised;
    public Boolean isDelivered;

    public Long classId;

    public StudentActivitiesDTO(Activity activity, Long classId, ActivityStatus status){
        this.id = activity.getId();
        this.name = activity.getName();
        this.maxPunctuation = activity.getPunctuation();
        this.expireDate = activity.getMaxDeliveryDate();
        this.teacherName = activity.getTeacher().getCredentials().getName();
        this.classId = classId;
        this.isRevised = status == ActivityStatus.REVISED;
        this.isDelivered = status == ActivityStatus.DELIVERED || this.isRevised;
    }
}

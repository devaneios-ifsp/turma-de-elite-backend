package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.Attachment;
import com.devaneios.turmadeelite.entities.SchoolClass;
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
public class StudentActivityDetailsDTO {
    public Long id;
    public String name;
    public String schoolClassName;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public LocalDateTime expireDate;

    public String description;
    public String filename;
    public String deliveryFilename;

    public StudentActivityDetailsDTO(Activity activity, SchoolClass schoolClass, String deliveryFilename){
        this.id = activity.getId();
        this.name = activity.getName();
        this.schoolClassName = schoolClass.getName();
        this.expireDate = activity.getMaxDeliveryDate();
        this.description = activity.getDescription();
        this.deliveryFilename = deliveryFilename;
        Attachment attachment = activity.getAttachment();
        if(attachment!=null){
            this.filename = attachment.getFilename();
        }
    }
}

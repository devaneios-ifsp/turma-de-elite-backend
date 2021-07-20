package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Activity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityViewDTO {
    public Long id;
    private String name;

    private String description;

    private Double punctuation;

    private Boolean isVisible;

    private Boolean isActive;

    private Boolean isDeliverable;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime maxDeliveryDate;

    private String filename;

    private List<SchoolClassNameDTO> classes;

    public ActivityViewDTO(Activity activity){
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.punctuation = activity.getPunctuation();
        this.isVisible = activity.getIsVisible();
        this.isActive = activity.getIsActive();
        this.isDeliverable = activity.getIsDeliverable();
        this.maxDeliveryDate = activity.getMaxDeliveryDate();
        this.filename = activity.getAttachment() != null ? activity.getAttachment().getFilename():null;
        this.classes = activity.getClasses() != null
                ? activity
                    .getClasses()
                    .stream()
                    .map(SchoolClassNameDTO::new)
                    .collect(Collectors.toList())
                : null;
    }
}

package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.SchoolClass;
import com.devaneios.turmadeelite.entities.TierConfig;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SchoolClassViewDTO {
    private Long id;
    private String externalId;
    private String name;
    private List<StudentMembershipDTO> students;
    private List<TeacherMembershipDTO> teachers;
    private Boolean isActive;
    private Boolean isDone;
    private Boolean isFromLms;
    private TierConfigDTO tierConfig;

    public SchoolClassViewDTO(SchoolClass schoolClass){
        this.id = schoolClass.getId();
        this.name = schoolClass.getName();

        this.students = schoolClass
                .getStudentsMemberships()
                .stream()
                .map(StudentMembershipDTO::new)
                .collect(Collectors.toList());

        this.teachers = schoolClass
                .getTeachersMemberships()
                .stream()
                .map(TeacherMembershipDTO::new)
                .collect(Collectors.toList());
        TierConfig tierConfig = schoolClass.getTierConfig();
        if(tierConfig!=null){
            this.tierConfig = new TierConfigDTO(tierConfig.getGoldPercent(),tierConfig.getSilverPercent(), tierConfig.getBronzePercent());
        }
        this.isActive = schoolClass.getIsActive();
        this.isDone = schoolClass.getIsDone();
    }
}

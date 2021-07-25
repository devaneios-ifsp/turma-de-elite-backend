package com.devaneios.turmadeelite.dto;

import com.devaneios.turmadeelite.entities.Tier;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudentRankingDTO implements Comparable<StudentRankingDTO>{
    @EqualsAndHashCode.Include
    private Long studentId;

    private Integer position;
    private Double grade;
    private String name;
    private Tier tier;


    @Override
    public int compareTo(StudentRankingDTO studentRankingDTO) {
        if(position!=null){
            return this.position.compareTo(studentRankingDTO.position);
        } else {
            return this.grade.compareTo(studentRankingDTO.getGrade());
        }
    }
}

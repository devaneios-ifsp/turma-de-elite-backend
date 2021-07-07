package com.devaneios.turmadeelite.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Achievement {

    @Id
    @Column(name = "student_id")
    private Long id;

    private String name;

    private LocalDateTime beforeAt;

    private Integer earlierOf;

    private Integer bestOf;

    private Integer averageGradeGreaterOrEqualsThan;

    private Long classId;
}
/*
* SELECT count(d) FROM ActivityDelivery d
* WHERE :beforeAt IS NULL OR d.deliveryTimestamp <:beforeAt
* AND earlierOf IS NULL OR (d.earlierOf - 1) =< ( SELECT count(d) FROM ActivityDelivery d  WHERE d.deliveryTimestamp > :me.deliveryTimestamp ORDER BY d.deliveryTimestamp DESC )
* AND bestOf IS NULL OR (d.bestOf - 1) =< ( SELECT count(d) FROM ActivityDelivery d  WHERE d.gradeReceived > :me.gradeReceived ORDER BY d.gradeReceived DESC )
* AND averageGradeGreaterThan IS NULL OR averageGradeGreaterOrEqualsThan < (SELECT average(d.gradeReceived) FROM ActivityDelivery d WHERE d.classId = :classId GROUP BY d.gradeReceived)
* */
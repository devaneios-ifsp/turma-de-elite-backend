package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Achievement;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.ActivityDelivery;
import com.devaneios.turmadeelite.entities.Student;

import com.devaneios.turmadeelite.repositories.AchievementRepository;
import com.devaneios.turmadeelite.repositories.ActivityDeliveryRepository;
import com.devaneios.turmadeelite.repositories.ActivityRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.services.DeliverAchievements;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DeliverAchievementsImpl implements DeliverAchievements {

    private final AchievementRepository achievementRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDeliveryRepository deliveryRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void deliverAchievements(Long classId) {
        List<Achievement> allAchievements = this.achievementRepository.findAll();
        List<Student> classStudents = this.studentRepository.findAllByClassId(classId);
        for(Achievement achievement:allAchievements){
            for(Student student: classStudents){
                List<ActivityDelivery> deliveries = this.deliveryRepository.findByStudentAndClass(student.getId(), classId);
                if(hasAchievement(achievement,deliveries)){
                    this.deliverAchievement(student.getId(),achievement);
                };
            }
        }
    }

    private void deliverAchievement(Long studentId,Achievement achievement){
        this.studentRepository.giveAchievement(studentId,achievement.getId());
    }

    private boolean hasAchievement(Achievement achievement, List<ActivityDelivery> activityDelivery){
        return beforeAt(achievement,activityDelivery)
                || earlierOf(achievement,activityDelivery)
                || bestOf(achievement,activityDelivery)
                || averageGradeGreaterOrEqualsThan(achievement,activityDelivery);
    }

    private boolean averageGradeGreaterOrEqualsThan(Achievement achievement, List<ActivityDelivery> activityDelivery) {
        Double minimumAverage = achievement.getAverageGradeGreaterOrEqualsThan();
        if(Objects.isNull(minimumAverage)){
            return false;
        }else{
            Double deliveriesAverage = activityDelivery
                    .stream()
                    .collect(Collectors.averagingDouble(ActivityDelivery::getGradeReceived));
            return minimumAverage <= deliveriesAverage;
        }
    }

    private boolean bestOf(Achievement achievement, List<ActivityDelivery> deliveries) {
        Integer bestOf = achievement.getBestOf();
        if(Objects.isNull(bestOf)){
            return true;
        }else{
            List<ActivityDelivery> attributable = deliveries
                    .stream()
                    .filter(delivery -> delivery.getActivity().getId() == achievement.getActivity().getId())
                    .collect(Collectors.toList());
            attributable.sort(Comparator.comparing(ActivityDelivery::getGradeReceived));
            if(attributable.size()>=(bestOf - 1)){
                ActivityDelivery delivery = deliveries.get(bestOf - 1);
                return Objects.nonNull(delivery);
            }
            return false;
        }
    }

    private boolean beforeAt(Achievement achievement, List<ActivityDelivery> deliveries){
        LocalDateTime beforeAt = achievement.getBeforeAt();
        if(Objects.isNull(beforeAt)){
            return true;
        }else{
            for(ActivityDelivery delivery: deliveries){
                boolean isFromThatActivity = achievement.getActivity().getId() == delivery.getActivity().getId();
                boolean isBefore = delivery.getDeliveryTimestamp().isBefore(beforeAt);
                if( isFromThatActivity && isBefore ){
                    return true;
                }
            }
            return false;
        }
    }

    private boolean earlierOf(Achievement achievement, List<ActivityDelivery> deliveries){
        Integer earlierOf = achievement.getEarlierOf();
        if(Objects.isNull(earlierOf)){
            return true;
        }else{
            deliveries.sort(Comparator.comparing(ActivityDelivery::getDeliveryTimestamp));
            if(deliveries.size() >= (earlierOf - 1)){
                ActivityDelivery delivery = deliveries.get(earlierOf - 1);
                return Objects.nonNull(delivery);
            }
            return false;
        }
    }
}
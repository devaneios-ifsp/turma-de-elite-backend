package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.StudentRankingDTO;
import com.devaneios.turmadeelite.entities.*;

import com.devaneios.turmadeelite.repositories.AchievementRepository;
import com.devaneios.turmadeelite.repositories.ActivityDeliveryRepository;
import com.devaneios.turmadeelite.repositories.ActivityRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.services.DeliverAchievements;
import com.google.common.collect.Comparators;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

            Activity activity = achievement.getActivity();
            SchoolClass schoolClass = achievement.getSchoolClass();
            System.out.println(schoolClass);

            if(activity !=null){
                List<ActivityDelivery> deliveriesForActivity = this.deliveryRepository.findStudentDeliveriesForActivityWithAttachment(activity.getId());
                giveActivityAchievementForEligible(achievement,deliveriesForActivity);
            }else{
                //Optional <Long> id = Optional.ofNullable(schoolClass.getId());
                System.out.println("entro no else");
                //this.giveClassAchievementForEligible(achievement,schoolClass.getId());
            }
        }
    }

    private void giveClassAchievementForEligible(Achievement achievement, Long classId) {
        List<Student> classStudents = this.studentRepository.findAllByClassId(classId);

        List<Activity> classActivities = this.activityRepository.findAllDoableActivitiesByClassId(classId);

        for(Student classStudent: classStudents){
            int activitiesFound = 0;
            Double totalReceived = 0D;

            for(Activity classActivity: classActivities){
                Long studentId = classStudent.getId();
                Long activityId = classActivity.getId();

                Double gradeReceivedForActivity = this.deliveryRepository
                        .findStudentDeliveryForActivityWithAttachment(studentId, activityId)
                        .map(ActivityDelivery::getGradeReceived)
                        .filter(Objects::nonNull)
                        .filter(percentageReceived -> percentageReceived > 0)
                        .map( percentageReceived -> (percentageReceived / 100) * classActivity.getPunctuation())
                        .orElse(0D);

                totalReceived += gradeReceivedForActivity;
                activitiesFound +=1;
            }

            Double averageGrade = activitiesFound != 0 ? totalReceived / activitiesFound : 0;

            if(averageGrade >= achievement.getAverageGradeGreaterOrEqualsThan()){
                this.deliverAchievement(classStudent.getId(),achievement);
            }
        }
    }

    private void deliverAchievement(Long studentId,Achievement achievement){
        this.studentRepository.giveAchievement(studentId,achievement.getId());
    }

    private void giveActivityAchievementForEligible(Achievement achievement, List<ActivityDelivery> activityDelivery){
        Set<Long> eligibleStudentsForBeforeAt = beforeAt(achievement,activityDelivery);
        Set<Long> eligibleStudentsEarlierOf = earlierOf(achievement, activityDelivery);
        Set<Long> eligibleStudentsBestOf = bestOf(achievement, activityDelivery);
        HashSet<Long> allEligibleStudents = new HashSet<>(eligibleStudentsBestOf);
        allEligibleStudents.addAll(eligibleStudentsForBeforeAt);
        allEligibleStudents.addAll(eligibleStudentsEarlierOf);

        for(Long studentId: allEligibleStudents){
            if(
                    eligibleStudentsForBeforeAt.contains(studentId)
                            && eligibleStudentsEarlierOf.contains(studentId)
                            && eligibleStudentsBestOf.contains(studentId)){
                this.deliverAchievement(studentId,achievement);
            }
        }
    }

    private Set<Long> bestOf(Achievement achievement, List<ActivityDelivery> deliveries) {
        Integer bestOf = achievement.getBestOf();
        if(Objects.isNull(bestOf)){
            return deliveries
                    .stream()
                    .map(ActivityDelivery::getStudent)
                    .map(Student::getId)
                    .collect(Collectors.toSet());
        }else{
            List<ActivityDelivery> attributable = deliveries
                    .stream()
                    .filter(delivery -> delivery.getActivity().getId().equals(achievement.getActivity().getId()))
                    .sorted(Comparator.comparing(ActivityDelivery::getGradeReceived, Comparator.reverseOrder()))
                    .collect(Collectors.toList());

            HashSet<Long> eligibleStudentIds = new HashSet<>();
            if(attributable.size()>=(bestOf - 1)){
                ActivityDelivery delivery = attributable.get(bestOf - 1);
                if(Objects.nonNull(delivery)){
                    eligibleStudentIds.add(delivery.getStudent().getId());
                }
            }
            return eligibleStudentIds;
        }
    }

    private Set<Long> beforeAt(Achievement achievement, List<ActivityDelivery> deliveries){
        LocalDateTime beforeAt = achievement.getBeforeAt();
        if(Objects.isNull(beforeAt)){
            return deliveries
                    .stream()
                    .map(ActivityDelivery::getStudent)
                    .map(Student::getId)
                    .collect(Collectors.toSet());
        }else{
            Set<Long> eligibleStudentsIds = new HashSet<>();
            for(ActivityDelivery delivery: deliveries){
                boolean isFromThatActivity = achievement.getActivity().getId().equals(delivery.getActivity().getId());
                boolean isBefore = delivery.getDeliveryTimestamp().isBefore(beforeAt);
                if( isFromThatActivity && isBefore ){
                    eligibleStudentsIds.add(delivery.getStudent().getId());
                }
            }
            return eligibleStudentsIds;
        }
    }

    private Set<Long> earlierOf(Achievement achievement, List<ActivityDelivery> deliveries){
        Integer earlierOf = achievement.getEarlierOf();
        if(Objects.isNull(earlierOf)){
            return deliveries
                    .stream()
                    .map(ActivityDelivery::getStudent)
                    .map(Student::getId)
                    .collect(Collectors.toSet());
        }else{
            deliveries.sort(Comparator.comparing(ActivityDelivery::getDeliveryTimestamp));
            Set<Long> eligibleStudentsIds = new HashSet<>();
            if(deliveries.size() >= (earlierOf - 1)){
                ActivityDelivery delivery = deliveries.get(earlierOf - 1);
                if(Objects.nonNull(delivery)){
                    eligibleStudentsIds.add(delivery.getStudent().getId());
                }
            }
            return eligibleStudentsIds;
        }
    }
}
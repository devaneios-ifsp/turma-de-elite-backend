package com.devaneios.turmadeelite.external.classroom;

import com.devaneios.turmadeelite.entities.Achievement;
import com.devaneios.turmadeelite.external.ExternalDeliverAchievements;
import com.devaneios.turmadeelite.external.classroom.activities.ClassroomActivities;
import com.devaneios.turmadeelite.external.classroom.courses.ClassroomCoursesService;
import com.devaneios.turmadeelite.external.classroom.students.ClassroomStudents;
import com.devaneios.turmadeelite.external.domain.ExternalStudentGrade;
import com.devaneios.turmadeelite.repositories.AchievementRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.StudentSubmission;
import com.google.api.services.classroom.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClassroomDeliverAchievementsImpl implements ExternalDeliverAchievements {

    private final AchievementRepository achievementRepository;
    private final ClassroomCoursesService classroomCoursesService;
    private final ClassroomServiceFactory classroomServiceFactory;
    private final ClassroomActivities classroomActivities;
    private final ClassroomStudents classroomStudents;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Override
    public void deliverAchievementsToStudentsInCourse(String externalAuthUuid,String courseId) throws IOException {
        Classroom service = classroomServiceFactory.getService(externalAuthUuid);

        List<Achievement> allAchievements = this.achievementRepository.findAll();
        for(Achievement achievement: allAchievements){
            String activityId = achievement.getExternalActivityId();
            String schoolClassId = achievement.getExternalSchoolClassId();

            if(activityId != null){

            }else{
                this.giveClassAchievementForEligible(achievement,externalAuthUuid,schoolClassId);
            }
        }
    }

    private void giveClassAchievementForEligible(Achievement achievement,String authUuid, String schoolClassId) throws IOException {
        List<ExternalStudentGrade> studentsGrades = this.classroomActivities.getStudentsGrades(authUuid, schoolClassId);
        studentsGrades.forEach(studentGrade -> {
            if(studentGrade.grade >= achievement.getAverageGradeGreaterOrEqualsThan()){
                this.deliverAchievement(authUuid,studentGrade.externalUserId,achievement);
            }
        });
    }

    private void giveActivityAchievementForEligible(String authUuid,Achievement achievement, String courseId, String activityId) throws IOException {
        List<StudentSubmission> submissions = this.classroomActivities.getSubmissions(authUuid, courseId, activityId);
        Set<String> eligibleStudentsForBeforeAt = beforeAt(achievement,submissions);
        Set<String> eligibleStudentsForEarlierOf = earlierOf(achievement, submissions);
        Set<String> eligibleStudentsForBestOf = bestOf(achievement,submissions);
        Set<String> allEligibleStudents = new HashSet<>(eligibleStudentsForBeforeAt);
        allEligibleStudents.addAll(eligibleStudentsForBestOf);
        allEligibleStudents.addAll(eligibleStudentsForEarlierOf);
        for(String studentId: allEligibleStudents){
            if(eligibleStudentsForBeforeAt.contains(studentId)
                && eligibleStudentsForBestOf.contains(studentId)
                && eligibleStudentsForEarlierOf.contains(studentId)){
                this.deliverAchievement(authUuid,studentId,achievement);
            }
        }
    }

    private Set<String> beforeAt(Achievement achievement,List<StudentSubmission> classroomSubmissions) throws IOException {
        LocalDateTime beforeAt = achievement.getBeforeAt();
        if(Objects.isNull(beforeAt)){
            return classroomSubmissions
                    .stream()
                    .map(StudentSubmission::getUserId)
                    .collect(Collectors.toSet());
        } else {
            Set<String> eligibleStudentsId = new HashSet<>();
            for(StudentSubmission studentSubmission: classroomSubmissions){
                boolean isFromThatActivity = achievement.getExternalActivityId().equals(studentSubmission.getCourseWorkId());
                LocalDateTime submissionLastUpdate = LocalDateTime.parse(studentSubmission.getUpdateTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                boolean isBefore = submissionLastUpdate.isBefore(beforeAt);
                if(isFromThatActivity && isBefore){
                    eligibleStudentsId.add(studentSubmission.getUserId());
                }
            }
            return eligibleStudentsId;
        }
    }

    private Set<String> bestOf(Achievement achievement,List<StudentSubmission> classroomSubmissions) throws IOException {
        Integer bestOf = achievement.getBestOf();
        if(Objects.isNull(bestOf)){
            return classroomSubmissions
                    .stream()
                    .map(StudentSubmission::getUserId)
                    .collect(Collectors.toSet());
        }else{
            List<StudentSubmission> attributableSubmissions = classroomSubmissions
                    .stream()
                    .filter(studentSubmission -> studentSubmission.getCourseWorkId().equals(achievement.getExternalActivityId()))
                    .sorted(Comparator.comparing(StudentSubmission::getAssignedGrade, Comparator.reverseOrder()))
                    .collect(Collectors.toList());
            Set<String> eligibleStudentsIds = new HashSet<>(attributableSubmissions.size());
            if(attributableSubmissions.size()>=(bestOf - 1)){
                StudentSubmission studentSubmission = attributableSubmissions.get(bestOf - 1);
                if(Objects.nonNull(studentSubmission)){
                    eligibleStudentsIds.add(studentSubmission.getUserId());
                }
            }
            return eligibleStudentsIds;
        }
    }

    private Set<String> earlierOf(Achievement achievement,List<StudentSubmission> classroomSubmissions) throws IOException {
        Integer earlierOf = achievement.getEarlierOf();
        if(Objects.isNull(earlierOf)){
            return classroomSubmissions
                    .stream()
                    .map(StudentSubmission::getUserId)
                    .collect(Collectors.toSet());
        } else {
            classroomSubmissions
                    .sort(Comparator
                            .comparing(studentSubmission -> LocalDateTime.parse(
                                    studentSubmission.getUpdateTime(),
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                )
                            )
                    );
            Set<String> eligibleStudentsIds = new HashSet<>(classroomSubmissions.size());
            if(classroomSubmissions.size() >= (earlierOf - 1)){
                StudentSubmission studentSubmission = classroomSubmissions.get(earlierOf - 1);
                if(Objects.nonNull(studentSubmission)){
                    eligibleStudentsIds.add(studentSubmission.getUserId());
                }
            }
            return eligibleStudentsIds;
        }
    }

    @SneakyThrows
    private void deliverAchievement(String authUuid,String externalUserId, Achievement achievement) {
        Classroom service = classroomServiceFactory.getService(authUuid);

        UserProfile externalUser = service
                .userProfiles()
                .get(externalUserId)
                .execute();

        this.userRepository.findByEmail(externalUser.getEmailAddress()).ifPresent(user -> {
            this.studentRepository.giveAchievement(user.getId(),achievement.getId());
        });
    }
}

package com.devaneios.turmadeelite.external.classroom.activities;

import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.domain.ExternalStudentGrade;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClassroomActivities {

    private final ClassroomServiceFactory classroomServiceFactory;

    public List<CourseWork> getAllClassActivities(String authUuid, String courseId) throws IOException {
        Classroom service = this.classroomServiceFactory.getService(authUuid);
        List<ListCourseWorkResponse> responseList = new LinkedList<>();
        ListCourseWorkResponse courseWorkResponse = service
                .courses()
                .courseWork()
                .list(courseId)
                .execute();


        if(courseWorkResponse!=null){
            responseList.add(courseWorkResponse);
            String nextPageToken = courseWorkResponse.getNextPageToken();
            while(StringUtils.hasText(nextPageToken)){
                ListCourseWorkResponse nextPage = service
                        .courses()
                        .courseWork()
                        .list(courseId)
                        .setPageToken(nextPageToken)
                        .execute();
                if(nextPage!=null){
                    responseList.add(nextPage);
                    nextPageToken = nextPage.getNextPageToken();
                    if(!StringUtils.hasText(nextPageToken)){
                        break;
                    }
                }else{
                    break;
                }
            }
        }

        return responseList
                .stream()
                .map(ListCourseWorkResponse::getCourseWork)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<ExternalStudentGrade> getStudentsGrades(String authUuid, String courseId) throws IOException {
        Classroom service = this.classroomServiceFactory.getService(authUuid);
        List<CourseWork> allClassActivities = this.getAllClassActivities(authUuid, courseId);
        //Student id , Submissions
        Map<String,List<Double>> studentSubmissionsMap = new LinkedHashMap<>();
        int activitiesQuantity = 0;
        for(CourseWork courseWork: allClassActivities){
            Double maxPoints = courseWork.getMaxPoints();
            activitiesQuantity++;
            Map<String, List<StudentSubmission>> studentSubmissions = this.getSubmissions(authUuid, courseId, courseWork.getId())
                    .stream()
                    .collect(Collectors.groupingBy(StudentSubmission::getUserId));
            studentSubmissions.forEach((studentId, submissions)->{
                submissions.forEach(submission -> {
                    Double gradePercentage = (submission.getAssignedGrade() / maxPoints) * 100;
                    studentSubmissionsMap.compute(studentId,(studentKey, studentGrades) -> {
                        if(studentGrades == null){
                            LinkedList<Double> grades = new LinkedList<>();
                            grades.add(gradePercentage);
                            return grades;
                        }else{
                            studentGrades.add(gradePercentage);
                            return studentGrades;
                        }
                    });
                });

            });
        }
        ArrayList<ExternalStudentGrade> studentGrades = new ArrayList<>(studentSubmissionsMap.size());
        studentSubmissionsMap.forEach((studentId,grades)->{
            if(grades.size() > 0){
                studentGrades.add(
                        ExternalStudentGrade
                        .builder()
                                .grade(grades
                                        .stream()
                                        .mapToDouble(Double::doubleValue)
                                        .sum())
                                .externalUserId(studentId)
                        .build()
                );
            }else{
                studentGrades.add(
                        ExternalStudentGrade
                        .builder()
                        .grade(0D)
                        .externalUserId(studentId)
                        .build()
                );
            }
        });
        return studentGrades;
    }

    public List<StudentSubmission> getSubmissions(String authUuid, String courseId, String courseWorkId) throws IOException {
        Classroom service = this.classroomServiceFactory.getService(authUuid);
        ListStudentSubmissionsResponse listStudentSubmissionsResponse = service
                .courses()
                .courseWork()
                .studentSubmissions()
                .list(courseId, courseWorkId)
                .execute();
        List<ListStudentSubmissionsResponse> responseList = new LinkedList<>();
        if(listStudentSubmissionsResponse!=null){
            String nextPageToken = listStudentSubmissionsResponse.getNextPageToken();
            responseList.add(listStudentSubmissionsResponse);

            while(StringUtils.hasText(nextPageToken)){
                ListStudentSubmissionsResponse nextPage = service
                        .courses()
                        .courseWork()
                        .studentSubmissions()
                        .list(courseId, courseWorkId)
                        .setPageToken(nextPageToken)
                        .execute();
                if(nextPage!=null){
                    responseList.add(nextPage);
                    nextPageToken = nextPage.getNextPageToken();
                    if(!StringUtils.hasText(nextPageToken)){
                        break;
                    }
                }else{
                    break;
                }

            }
        }
        return responseList
                .stream()
                .map(ListStudentSubmissionsResponse::getStudentSubmissions)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}

package com.devaneios.turmadeelite.external.classroom.students;

import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.ListStudentsResponse;
import com.google.api.services.classroom.model.Student;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClassroomStudents {
    private final ClassroomServiceFactory classroomServiceFactory;

    public List<Student> getStudentsFromCourse(String authUuid, String courseId) throws IOException {
        Classroom classroom = this.classroomServiceFactory.getService(authUuid);
        List<ListStudentsResponse> responseList = new LinkedList<>();
        ListStudentsResponse studentsResponse = classroom
                .courses()
                .students()
                .list(courseId)
                .execute();

        String nextPageToken = studentsResponse.getNextPageToken();

        if(studentsResponse!=null) responseList.add(studentsResponse);

        while(StringUtils.hasText(nextPageToken)){
            ListStudentsResponse nextPageResponse = classroom
                    .courses()
                    .students()
                    .list(courseId)
                    .setPageToken(nextPageToken)
                    .execute();
            if(nextPageResponse!=null){
                responseList.add(nextPageResponse);
                nextPageToken = nextPageResponse.getNextPageToken();
                if(!StringUtils.hasText(nextPageToken)){
                    break;
                }
            }else{
                break;
            }
        }

        try {
            return responseList
                    .stream()
                    .map(ListStudentsResponse::getStudents)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } catch (NullPointerException e){
            return new LinkedList<>();
        }


    }
}

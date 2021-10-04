package com.devaneios.turmadeelite.classroom.classes;

import com.devaneios.turmadeelite.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.ListCoursesResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ClassesService {

    private ClassroomServiceFactory serviceFactory;

    public List<SchoolClassViewDTO> getAllClasses(String authUuid) throws IOException {
        Classroom service = this.serviceFactory.getService(authUuid);
        List<ListCoursesResponse> allClassesResponse = new LinkedList<>();
        ListCoursesResponse coursesResponse = service
                                        .courses()
                                        .list()
                                        .execute();
        String nextPageToken = coursesResponse.getNextPageToken();

        while(nextPageToken != null && !nextPageToken.equals("")){
            ListCoursesResponse nextPageResponse = service.courses().list().setPageToken(nextPageToken).execute();
            allClassesResponse.add(nextPageResponse);
            nextPageToken = nextPageResponse.getNextPageToken();
        }

        return allClassesResponse
                .stream()
                .map(ListCoursesResponse::getCourses)
                .flatMap(List::stream)
                .map( classroomClass ->
                    SchoolClassViewDTO
                            .builder()
                            .externalId(classroomClass.getId())
                            .name(classroomClass.getName())
                            .isActive(classroomClass.getCourseState().equals("ACTIVE"))
                            .isDone(false)
                            .build()
                )
                .collect(Collectors.toList());
    }
}

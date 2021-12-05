package com.devaneios.turmadeelite.external.classroom.courses;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.courses.ExternalCoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CoursesService implements ExternalCoursesService {

    private ClassroomServiceFactory serviceFactory;

    public List<SchoolClassViewDTO> getAllCourses(String authUuid) throws IOException {
        try {
            Classroom service = this.serviceFactory.getService(authUuid);
            List<ListCoursesResponse> allClassesResponse = new LinkedList<>();
            ListCoursesResponse coursesResponse = service
                    .courses()
                    .list()
                    .execute();
            String nextPageToken = coursesResponse.getNextPageToken();

            if (coursesResponse != null) allClassesResponse.add(coursesResponse);

            while (nextPageToken != null && !nextPageToken.equals("")) {
                ListCoursesResponse nextPageResponse = service.courses().list().setPageToken(nextPageToken).execute();
                allClassesResponse.add(nextPageResponse);
                nextPageToken = nextPageResponse.getNextPageToken();
            }

            return allClassesResponse
                    .stream()
                    .map(ListCoursesResponse::getCourses)
                    .flatMap(List::stream)
                    .map(classroomClass ->
                            SchoolClassViewDTO
                                    .builder()
                                    .externalId(classroomClass.getId())
                                    .name(classroomClass.getName())
                                    .isActive(classroomClass.getCourseState().equals("ACTIVE"))
                                    .isDone(false)
                                    .build()
                    )
                    .collect(Collectors.toList());

        } catch (GoogleJsonResponseException e) {
            GoogleJsonError details = e.getDetails();
            if(details.getCode() == 401){
                throw new ExternalServiceAuthenticationException();
            }else{
                throw e;
            }
        }
    }

    @Override
    public List<SchoolClassViewDTO> getCoursesFromTeacher(String authUuid) throws IOException {

        Classroom service = this.serviceFactory.getService(authUuid);
        List<ListCoursesResponse> allTeacherClassesResponse = new LinkedList<>();
        UserProfile authenticatedUser = service.userProfiles().get("me").execute();
        String externalId = authenticatedUser.getId();

        ListCoursesResponse teacherCoursesResponse = service
                .courses()
                .list()
                .setTeacherId(externalId)
                .execute();
        String nextPageToken = teacherCoursesResponse.getNextPageToken();

        if (allTeacherClassesResponse != null) allTeacherClassesResponse.add(teacherCoursesResponse);

        while (nextPageToken != null && !nextPageToken.equals("")) {
            ListCoursesResponse nextPageResponse = service.courses().list().setPageToken(nextPageToken).execute();
            allTeacherClassesResponse.add(nextPageResponse);
            nextPageToken = nextPageResponse.getNextPageToken();
        }

        return allTeacherClassesResponse
                .stream()
                .map(ListCoursesResponse::getCourses)
                .flatMap(List::stream)
                .map(classroomTeacherClass ->
                        SchoolClassViewDTO
                                .builder()
                                .id(null)
                                .externalId(classroomTeacherClass.getId())
                                .name(classroomTeacherClass.getName())
                                .isActive(classroomTeacherClass.getCourseState().equals("ACTIVE"))
                                .isDone(false)
                                .isFromLms(true)
                                .build()
                )
                .collect(Collectors.toList());

    }

}

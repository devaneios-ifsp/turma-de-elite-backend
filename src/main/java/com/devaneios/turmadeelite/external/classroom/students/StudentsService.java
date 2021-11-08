package com.devaneios.turmadeelite.external.classroom.students;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.classroom.courses.CoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.devaneios.turmadeelite.external.students.ExternalStudentsService;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.ListStudentsResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentsService implements ExternalStudentsService {

    private final CoursesService coursesService;
    private ClassroomServiceFactory serviceFactory;

    @Override
    public List<SchoolUserViewDTO> getAllStudents(String authUuid) throws IOException {
        try {
            Classroom service = this.serviceFactory.getService(authUuid);
            List<ListStudentsResponse> allStudentsResponse = new LinkedList<>();
            List<SchoolClassViewDTO> classroomCourses = coursesService.getAllCourses(authUuid);
            ListStudentsResponse nextPageResponse = new ListStudentsResponse();

            ListStudentsResponse studentsResponse = service
                    .courses()
                    .students()
                    .list(classroomCourses.get(0).getExternalId())
                    .execute();
            String nextPageToken = studentsResponse.getNextPageToken();

            if (studentsResponse != null) {
                allStudentsResponse.add(studentsResponse);
            }

            for (SchoolClassViewDTO classroomCourse : classroomCourses) {
                while (nextPageToken != null && !nextPageToken.equals("")) {
                    nextPageResponse = service.courses().students().list(classroomCourse.getExternalId()).setPageToken(nextPageToken).execute();
                    if (nextPageResponse != null) {
                        allStudentsResponse.add(nextPageResponse);
                    }
                }
                nextPageToken = nextPageResponse.getNextPageToken();
            }

            return allStudentsResponse
                    .stream()
                    .map(ListStudentsResponse::getStudents)
                    .flatMap(List::stream)
                    .map(classroomStudent ->
                            SchoolUserViewDTO
                                    .builder()
                                    .externalId(classroomStudent.getUserId())
                                    .name(classroomStudent.getProfile().getName().getFullName())
                                    .email(classroomStudent.getProfile().getEmailAddress())
                                    .school(null)
                                    .isActive(true)
                                    .build()
                    )
                    .collect(Collectors.toList());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError details = e.getDetails();
            if (details.getCode() == 401) {
                throw new ExternalServiceAuthenticationException();
            } else {
                throw e;
            }
        }
    }
}

package com.devaneios.turmadeelite.external.classroom.students;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.dto.StudentMembershipDTO;
import com.devaneios.turmadeelite.dto.StudentViewDTO;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.classroom.courses.CoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.devaneios.turmadeelite.external.students.ExternalStudentsService;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.ListStudentsResponse;
import com.google.api.services.classroom.model.UserProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

            ListStudentsResponse studentsResponse = service
                    .courses()
                    .students()
                    .list(classroomCourses.get(0).getExternalId())
                    .execute();
            String nextPageToken = studentsResponse.getNextPageToken();

            if (studentsResponse != null && studentsResponse.size() > 0) {
                allStudentsResponse.add(studentsResponse);
            }

            for (SchoolClassViewDTO classroomCourse : classroomCourses) {
                    ListStudentsResponse nextPageResponse = service.courses().students().list(classroomCourse.getExternalId()).setPageToken(nextPageToken).execute();
                    if (nextPageResponse != null && nextPageResponse.size() > 0) {
                        allStudentsResponse.add(nextPageResponse);
                    }
                    assert nextPageResponse != null;
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

    @Override
    public StudentViewDTO getStudentByExternalId(String externalId, String authUuid) throws IOException {
        Classroom service = this.serviceFactory.getService(authUuid);
        UserProfile student = service.userProfiles().get(externalId).execute();

        return StudentViewDTO.builder()
                .id(null)
                .externalId(student.getId())
                .email(student.getEmailAddress())
                .name(student.getName().getFullName())
                .registry(student.getId())
                .isActive(true)
                .isFromLms(true)
                .build();

    }

    public List<StudentViewDTO> getStudentsFromClass(String classId, String authUuid) throws IOException {
        Classroom service = this.serviceFactory.getService(authUuid);
        List<ListStudentsResponse> allClassStudentsResponse = new LinkedList<>();
        Course classroom = service
                .courses()
                .get(classId)
                .execute();
        ListStudentsResponse classStudentsResponse = service
                .courses()
                .students()
                .list(classId)
                .execute();

        String nextPageToken = classStudentsResponse.getNextPageToken();

        if (classStudentsResponse != null && classStudentsResponse.size() > 0) {
            allClassStudentsResponse.add(classStudentsResponse);
        }

        while (nextPageToken != null && !nextPageToken.equals("")) {
            ListStudentsResponse nextPageResponse = service.courses().students().list(classId).setPageToken(nextPageToken).execute();
            allClassStudentsResponse.add(nextPageResponse);
            nextPageToken = nextPageResponse.getNextPageToken();
        }

        return allClassStudentsResponse
                .stream()
                .map(ListStudentsResponse::getStudents)
                .flatMap(List::stream)
                .map(classroomClassStudent ->
                        StudentViewDTO
                                .builder()
                                .externalId(classroomClassStudent.getUserId())
                                .name(classroomClassStudent.getProfile().getName().getFullName())
                                .email(classroomClassStudent.getProfile().getEmailAddress())
                                .registry(classroomClassStudent.getUserId())
                                .isActive(true)
                                .isFromLms(true)
                                .build()
                ).collect(Collectors.toList());
    }
}

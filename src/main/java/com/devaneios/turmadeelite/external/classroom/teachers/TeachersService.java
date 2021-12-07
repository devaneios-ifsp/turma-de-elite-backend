package com.devaneios.turmadeelite.external.classroom.teachers;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.courses.ExternalCoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.devaneios.turmadeelite.external.teachers.ExternalTeachersService;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.ListTeachersResponse;
import com.google.api.services.classroom.model.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeachersService implements ExternalTeachersService {

    private final ExternalCoursesService coursesService;
    private ClassroomServiceFactory classroomServiceFactory;

    @Override
    public List<SchoolUserViewDTO> getAllTeachers(String authUuid) throws IOException {
        try {
            Classroom service = this.classroomServiceFactory.getService(authUuid);
            List<SchoolClassViewDTO> classroomCourses = coursesService.getAllCourses(authUuid);
            List<ListTeachersResponse> allTeachersResponse = new LinkedList<>();

            for (SchoolClassViewDTO classroomCourse : classroomCourses) {
                ListTeachersResponse teachersResponse = service
                        .courses()
                        .teachers()
                        .list(classroomCourse
                                .getExternalId())
                        .execute();
                if (teachersResponse != null) {
                    allTeachersResponse.add(teachersResponse);
                }
            }

            List<SchoolUserViewDTO> teachers = allTeachersResponse
                    .stream()
                    .map(ListTeachersResponse::getTeachers)
                    .flatMap(List::stream)
                    .map(SchoolUserViewDTO::fromClassroom
                    )
                    .collect(Collectors.toList());

            for (int i = 0; i < teachers.size(); i++){
                for(int j = 0; j < teachers.size(); j++) {
                    if(!(i == j) && teachers.get(i).equals(teachers.get(j))){
                        teachers.remove(j);
                    }
                }
            }

            return teachers;
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
    public SchoolUserViewDTO getTeacherByExternalId(String externalId, String authUuid) throws IOException {
        Classroom service = this.classroomServiceFactory.getService(authUuid);
        UserProfile teacher = service.userProfiles().get(externalId).execute();

        return SchoolUserViewDTO.
                builder()
                .externalId(teacher.getId())
                .email(teacher.getEmailAddress())
                .name(teacher.getName().getFullName())
                .school(null)
                .isActive(true)
                .isFromLms(true)
                .build();
    }
}

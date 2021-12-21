package com.devaneios.turmadeelite.external.classroom.courses;

import com.devaneios.turmadeelite.dto.*;
import com.devaneios.turmadeelite.entities.ExternalClassConfig;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.classroom.students.ClassroomStudents;
import com.devaneios.turmadeelite.external.courses.ExternalCoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.devaneios.turmadeelite.repositories.ExternalClassConfigRepository;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ClassroomCoursesService implements ExternalCoursesService {

    private ClassroomServiceFactory serviceFactory;
    private ClassroomStudents classroomStudents;
    private ExternalClassConfigRepository classConfigRepository;

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

    @Override
    public SchoolClassViewDTO findCourseById(String courseId, String authUuid) throws IOException {
        Classroom service = this.serviceFactory.getService(authUuid);
        Course courseResponse = service
                .courses()
                .get(courseId)
                .execute();

        List<Student> students = this.classroomStudents
                .getStudentsFromCourse(authUuid, courseId);

        List<Teacher> teachers = this.getAllTeacherByCourseId(authUuid,courseId);
        Optional<ExternalClassConfig> optionalTierConfig = this.classConfigRepository
                .findByExternalClassId(courseId);
        return SchoolClassViewDTO
                .builder()
                .externalId(courseResponse.getId())
                .name(courseResponse.getName())
                .students(students
                        .stream()
                        .map(StudentViewDTO::fromClassroom)
                        .map(studentViewDTO -> StudentMembershipDTO
                                .builder()
                                .student(studentViewDTO)
                                .isActive(true)
                                .build())
                        .collect(Collectors.toList()))
                .teachers(
                    teachers
                            .stream()
                            .map(SchoolUserViewDTO::fromClassroom)
                            .map(teacher -> TeacherMembershipDTO
                                    .builder()
                                    .teacher(teacher)
                                    .isActive(true)
                                    .build())
                            .collect(Collectors.toList())
                )
                .tierConfig(optionalTierConfig
                        .map(tierConfig -> new TierConfigDTO(
                                tierConfig.getGoldPercent(),
                                tierConfig.getSilverPercent(),
                                tierConfig.getBronzePercent())
                            )
                        .orElseGet(TierConfigDTO::new))
                .isDone(optionalTierConfig
                        .map(ExternalClassConfig::getIsClosed)
                        .orElse(false))
                .build();
    }

    @Override
    public void saveTierConfig(TierConfigDTO tierConfigDTO, String classId) {
        ExternalClassConfig externalClassConfig = this.classConfigRepository
                .findByExternalClassId(classId)
                .orElseGet(ExternalClassConfig::new);

        externalClassConfig.setExternalClassId(classId);
        externalClassConfig.setIsClosed(externalClassConfig.getIsClosed()!=null?externalClassConfig.getIsClosed():false);
        externalClassConfig.setGoldPercent(tierConfigDTO.getGoldPercent());
        externalClassConfig.setSilverPercent(tierConfigDTO.getSilverPercent());
        externalClassConfig.setBronzePercent(tierConfigDTO.getBronzePercent());

        this.classConfigRepository.save(externalClassConfig);
    }

    public List<Teacher> getAllTeacherByCourseId(String authUuid, String courseId) throws IOException {
        Classroom service = this.serviceFactory.getService(authUuid);
        ListTeachersResponse teachersResponse = service
                .courses()
                .teachers()
                .list(courseId)
                .execute();
        List<ListTeachersResponse> responseList = new LinkedList<>();
        responseList.add(teachersResponse);
        String nextPageToken = teachersResponse.getNextPageToken();
        while (StringUtils.hasText(nextPageToken)){
            ListTeachersResponse nextPage = service
                    .courses()
                    .teachers()
                    .list(courseId)
                    .setPageToken(nextPageToken)
                    .execute();
            if(nextPage==null){
                break;
            }else{
                responseList.add(nextPage);
                nextPageToken = nextPage.getNextPageToken();
            }
        }
        return responseList
                .stream()
                .map(ListTeachersResponse::getTeachers)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /*
    * CREATE TABLE user_log_access(
    *   timestamp,
    *   user_id
    * );
    *
    * SELECT SUM(user_id),timestamp FROM user_log_access WHERE timestamp BETWEEN now() and now() - 90 dias group by timestamp,user_id
    *
    * */
}

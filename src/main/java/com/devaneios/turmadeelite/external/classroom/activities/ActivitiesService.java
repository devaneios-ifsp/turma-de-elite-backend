package com.devaneios.turmadeelite.external.classroom.activities;

import com.devaneios.turmadeelite.dto.ActivityViewDTO;
import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.StudentActivitiesDTO;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.classroom.courses.CoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.devaneios.turmadeelite.external.activities.ExternalActivitiesService;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.ListCourseWorkResponse;
import com.google.api.services.classroom.model.UserProfile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivitiesService implements ExternalActivitiesService {

    private final CoursesService coursesService;
    private ClassroomServiceFactory classroomServiceFactory;

    @Override
    public List<ActivityViewDTO> getActivitiesFromTeacher(String authUuid) throws IOException {
        try {
            Classroom service = this.classroomServiceFactory.getService(authUuid);
            List<SchoolClassViewDTO> classroomCourses = coursesService.getCoursesFromTeacher(authUuid);
            UserProfile authenticatedUser = service.userProfiles().get("me").execute();
            String externalId = authenticatedUser.getId();
            List<CourseWork> teacherActivities = new LinkedList<>();

            ListCourseWorkResponse activitiesResponse = service
                    .courses()
                    .courseWork()
                    .list(classroomCourses.get(classroomCourses.indexOf(classroomCourses.get(0))).getExternalId())
                    .execute();
            String nextPageToken = activitiesResponse.getNextPageToken();

            for (SchoolClassViewDTO classroomCourse : classroomCourses) {
                ListCourseWorkResponse nextPageResponse = service.courses().courseWork().list(classroomCourse.getExternalId()).setPageToken(nextPageToken).execute();
                if(nextPageResponse != null && nextPageResponse.size() > 0) {
                    List<CourseWork> classroomActivities = nextPageResponse.getCourseWork();
                    if (classroomActivities != null) {
                        for (CourseWork activity : classroomActivities) {
                            if (activity != null && Objects.equals(activity.getCreatorUserId(), externalId)) {
                                teacherActivities.add(activity);
                            }
                        }
                    }
                    nextPageToken = nextPageResponse.getNextPageToken();
                }
            }

            return teacherActivities
                    .stream()
                    .map(classroomActivity ->
                            ActivityViewDTO
                                    .builder()
                                    .externalId(classroomActivity.getId())
                                    .name(classroomActivity.getTitle())
                                    .description(classroomActivity.getDescription())
                                    .punctuation(classroomActivity.getMaxPoints())
                                    .maxDeliveryDate(null)
                                    .isDeliverable(false)
                                    .isVisible(false)
                                    .isActive(true)
                                    .filename("Visível no Classroom")
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
    public List<StudentActivitiesDTO> getActivitiesFromStudent(String authUuid) throws IOException {
        try {
            final Classroom service = this.classroomServiceFactory.getService(authUuid);
            List<SchoolClassViewDTO> classroomCourses = coursesService.getAllCourses(authUuid);
            UserProfile authenticatedUser = service.userProfiles().get("me").execute();
            String externalId = authenticatedUser.getId();
            List<CourseWork> studentActivities = new LinkedList<>();
            List<String> activityAuthors = new ArrayList<>();

            ListCourseWorkResponse activitiesResponse = service
                    .courses()
                    .courseWork()
                    .list(classroomCourses.get(classroomCourses.indexOf(classroomCourses.get(0))).getExternalId())
                    .execute();
            String nextPageToken = activitiesResponse.getNextPageToken();

            for (SchoolClassViewDTO classroomCourse : classroomCourses) {
                ListCourseWorkResponse nextPageResponse = service.courses().courseWork().list(classroomCourse.getExternalId()).setPageToken(nextPageToken).execute();
                List<CourseWork> classroomActivities = nextPageResponse.getCourseWork();
                if(classroomActivities != null) {
                    for (CourseWork activity : classroomActivities) {
                        if (activity != null && Objects.equals(activity.getCreatorUserId(), externalId)) {
                            studentActivities.add(activity);
                            UserProfile activityAuthor = service
                                    .userProfiles()
                                    .get(activity.getCreatorUserId())
                                    .execute();
                            activityAuthors.add(activityAuthor.getName().toString());
                        }
                    }
                }
                nextPageToken = nextPageResponse.getNextPageToken();
            }

            return studentActivities
                    .stream()
                    .map(classroomActivity ->
                            {
                                try {
                                    return StudentActivitiesDTO
                                            .builder()
                                            .id(null)
                                            .externalId(classroomActivity.getId())
                                            .name(classroomActivity.getTitle())
                                            .expireDate(null)
                                            .classId(null)
                                            .maxPunctuation(classroomActivity.getMaxPoints())
                                            .isDelivered(false)
                                            .isRevised(false)
                                            .teacherName(returnTeacherName(service, classroomActivity.getCreatorUserId()))
                                            .build();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
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
    public ActivityViewDTO getExternalActivityById(String externalId, String authUuid) throws IOException {
        List<ActivityViewDTO> teacherActivities = getActivitiesFromTeacher(authUuid);
        List<ActivityViewDTO> teacherActivity = teacherActivities
                .stream()
                .filter(activity -> Objects.equals(activity.getExternalId(), externalId))
                .collect(Collectors.toList());
        System.out.println(teacherActivity.get(0).getDescription());
        if (!teacherActivity.isEmpty()){
            return teacherActivity.get(0);
        }
        return null;
    }

    private String returnTeacherName(Classroom service, String creatorId) throws IOException {
        UserProfile activityAuthor = service
                .userProfiles()
                .get(creatorId)
                .execute();
        return activityAuthor.getName().getFullName();
    }
}

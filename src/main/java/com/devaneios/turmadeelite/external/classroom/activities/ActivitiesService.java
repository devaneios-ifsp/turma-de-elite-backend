package com.devaneios.turmadeelite.external.classroom.activities;

import com.devaneios.turmadeelite.dto.ActivityViewDTO;
import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.external.classroom.ClassroomServiceFactory;
import com.devaneios.turmadeelite.external.classroom.courses.CoursesService;
import com.devaneios.turmadeelite.external.exceptions.ExternalServiceAuthenticationException;
import com.devaneios.turmadeelite.external.activities.ExternalActivitiesService;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.ListCourseWorkResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ActivitiesService implements ExternalActivitiesService {

    private final CoursesService coursesService;
    private ClassroomServiceFactory classroomServiceFactory;

    @Override
    public List<ActivityViewDTO> getAllActivities(String authUuid) throws IOException {
        try {
            Classroom service = this.classroomServiceFactory.getService(authUuid);
            List<SchoolClassViewDTO> classroomCourses = coursesService.getAllCourses(authUuid);
            List<ListCourseWorkResponse> allActivitiesResponse = new LinkedList<>();
            ListCourseWorkResponse activitiesResponse = service
                    .courses()
                    .courseWork()
                    .list(classroomCourses.get(0).getExternalId())
                    .execute();

            String nextPageToken = activitiesResponse.getNextPageToken();

            if (activitiesResponse != null) allActivitiesResponse.add(activitiesResponse);

            for (SchoolClassViewDTO classroomCourse : classroomCourses) {
                while (nextPageToken != null && !nextPageToken.equals("")) {
                    ListCourseWorkResponse nextPageResponse = service.courses().courseWork().list(classroomCourse.getExternalId()).setPageToken(nextPageToken).execute();
                    allActivitiesResponse.add(nextPageResponse);
                    nextPageToken = nextPageResponse.getNextPageToken();
                }
            }

            return allActivitiesResponse
                    .stream()
                    .map(ListCourseWorkResponse::getCourseWork)
                    .flatMap(List::stream)
                    .map(classroomActivities ->
                            ActivityViewDTO
                                    .builder()
                                    .id(null)
                                    .name(classroomActivities.getTitle())
                                    .description(classroomActivities.getDescription())
                                    .filename(null)
                                    .punctuation(classroomActivities.getMaxPoints())
                                    .maxDeliveryDate(null)
                                    .isDeliverable(true)
                                    .isVisible(true)
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

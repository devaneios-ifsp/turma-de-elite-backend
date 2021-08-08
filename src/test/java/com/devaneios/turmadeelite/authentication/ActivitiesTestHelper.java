package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.mock.web.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ActivitiesTestHelper {

    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private final String teacherBearerToken;
    private final String studentBearerToken;

    public ActivitiesTestHelper(MockMvc mvc, ObjectMapper mapper, String teacherToken, String studentToken) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.teacherBearerToken = "Bearer " + teacherToken;
        this.studentBearerToken = "Bearer " + studentToken;
    }

    List<ActivityCreateDTO> createActivities() throws Exception{
        ActivityCreateDTO activity1 = ActivityCreateDTO
                .builder()
                .name("Matriz de transformação I")
                .description("Realize os exercícios do anexo referentes a matriz transformação")
                .schoolClasses(Arrays.asList(1L))
                .punctuation(50D)
                .isActive(true)
                .maxDeliveryDate("2022-01-01 12:00:00")
                .isVisible(true)
                .build();

        ActivityCreateDTO activity2 = ActivityCreateDTO
                .builder()
                .name("Matriz de transformação II")
                .description("Realize os exercícios do anexo referentes a matriz transformação")
                .schoolClasses(Arrays.asList(1L))
                .punctuation(50D)
                .isActive(true)
                .maxDeliveryDate("2022-01-01 12:00:00")
                .isVisible(true)
                .build();

        ActivityCreateDTO activity3 = ActivityCreateDTO
                .builder()
                .name("Matriz de transformação III")
                .description("Realize os exercícios do anexo referentes a matriz transformação")
                .schoolClasses(Arrays.asList(1L))
                .punctuation(50D)
                .isActive(true)
                .maxDeliveryDate("2022-01-01 12:00:00")
                .isVisible(true)
                .build();

        return Arrays.asList(activity1,activity2,activity3);
    }

    void saveActivities(List<ActivityCreateDTO> activities) throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "fis".getBytes());

        for(ActivityCreateDTO activity: activities){
            MockHttpServletRequestBuilder requestBuilder = fileUpload("/api/activities")
                    .file(multipartFile)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .param("name", activity.getName())
                    .param("description", activity.getName())
                    .param("schoolClasses", activity
                            .getSchoolClasses()
                            .stream()
                            .map(aLong -> aLong.toString())
                            .collect(Collectors.joining())
                    )
                    .param("punctuation", activity.getPunctuation().toString())
                    .param("isActive", activity.getIsActive().toString())
                    .param("maxDeliveryDate", activity.getMaxDeliveryDate())
                    .param("isVisible", activity.getIsVisible().toString())
                    .header("Authorization", teacherBearerToken);

            mvc.perform(
                    requestBuilder)
                    .andExpect(status().isCreated());
        }
    }

    public void activitiesCanBeListedPaginated() throws Exception {
        mvc.perform(
                get("/api/activities?pageSize=3&pageNumber=0")
                        .header("Authorization", teacherBearerToken)
        )
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(status().isOk());
    }

    public void activitiesCanBeListed() throws Exception {
        mvc.perform(
                get("/api/activities")
                        .header("Authorization", teacherBearerToken)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    public void getStudentActivities() throws Exception{
        mvc.perform(get("/api/activities/student")
                .header("Authorization",studentBearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThan(0)));
    }

    public void updateActivity(Long id) throws Exception{
        mvc.perform(get("/api/activities/"+id)
                .header("Authorization",teacherBearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Matchers.greaterThan(0)));

        ActivityCreateDTO activity = ActivityCreateDTO
                .builder()
                .name("Matriz de transformação I")
                .description("Realize os exercícios do anexo referentes a matriz transformação")
                .schoolClasses(Arrays.asList(1L))
                .punctuation(70D)
                .isActive(true)
                .maxDeliveryDate("2022-01-02 12:00:00")
                .isVisible(true)
                .build();
        MockMultipartFile multipartFile = new MockMultipartFile("file", "fis".getBytes());

        MockHttpServletRequestBuilder requestBuilder = put("/api/activities/"+id)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("document",multipartFile)
                .param("name", activity.getName())
                .param("description", activity.getName())
                .param("schoolClasses", activity
                        .getSchoolClasses()
                        .stream()
                        .map(aLong -> aLong.toString())
                        .collect(Collectors.joining())
                )
                .param("punctuation", activity.getPunctuation().toString())
                .param("isActive", activity.getIsActive().toString())
                .param("maxDeliveryDate", activity.getMaxDeliveryDate())
                .param("isVisible", activity.getIsVisible().toString())
                .header("Authorization", teacherBearerToken);

        mvc.perform(
                requestBuilder)
                .andExpect(status().isOk());
    }

    public void getActivitiesDetails(int activityId, int classId) throws Exception {
        mvc.perform(
                get("/api/activities/" + activityId + "/class/" + classId + "/student")
                .header("Authorization",studentBearerToken))
            .andExpect(status().isOk());
    }

    public void downloadTeacherActivityAttachment(Long id) throws Exception {
        mvc.perform(
                get("/api/activities/"+ id + "/download")
                .header("Authorization",teacherBearerToken)
        ).andExpect(status().isOk());
    }

    public void downloadStudentActivityAttachment(Long id) throws Exception {
        mvc.perform(
                get("/api/activities/"+ id + "/student/download")
                        .header("Authorization",studentBearerToken)
        ).andExpect(status().isOk());
    }


    public void deliveryGrade(int deliveryId, Float grade) throws Exception {
        mvc.perform(
                post("/api/activity-deliveries/" + deliveryId + "/grade")
                .header("Authorization",teacherBearerToken)
                .content(grade.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
        ).andExpect(status().isCreated());
    }

    public void downloadStudentActivityDelivery(int activityId) throws Exception {
        mvc.perform(
                get("/api/activity-deliveries/student/activity/" + activityId + "/download")
                        .header("Authorization",studentBearerToken)
        ).andExpect(status().isOk());
    }
}

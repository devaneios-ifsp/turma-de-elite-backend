package com.devaneios.turmadeelite.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ActivityDeliveryTestHelper {
    private final MockMvc mvc;
    private final ObjectMapper mapper;
    private final String studentBearerToken;
    private final String teacherBearerToken;
    public ActivityDeliveryTestHelper(MockMvc mvc, ObjectMapper mapper, String studentToken, String teacherToken) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.studentBearerToken = "Bearer " + studentToken;
        this.teacherBearerToken = "Bearer " + teacherToken;
    }

    public void deliveryActivity(Long id) throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("document", "fis".getBytes());

        this.mvc.perform(
                fileUpload("/api/activity-deliveries/activity/"+id)
                .file("document",multipartFile.getBytes())
                .header("Authorization",studentBearerToken)
        ).andExpect(status().isCreated());
    }
}

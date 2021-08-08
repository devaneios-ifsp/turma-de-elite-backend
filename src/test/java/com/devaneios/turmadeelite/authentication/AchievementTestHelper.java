package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.AchievementCreateDTO;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

public class AchievementTestHelper {
    private final MockMvc mvc;
    private final String teacherBearerToken;
    private final String studentBearerToken;

    public AchievementTestHelper(MockMvc mvc, String teacherBearerToken, String studentBearerToken) {
        this.mvc = mvc;
        this.teacherBearerToken = teacherBearerToken;
        this.studentBearerToken = studentBearerToken;
    }
}

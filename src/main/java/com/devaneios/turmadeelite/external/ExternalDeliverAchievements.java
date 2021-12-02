package com.devaneios.turmadeelite.external;

import java.io.IOException;

public interface ExternalDeliverAchievements {
    void deliverAchievementsToStudentsInCourse(String externalAuthUuid,String courseId) throws IOException;
}

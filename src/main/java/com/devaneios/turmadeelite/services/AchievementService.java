package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.AchievementCreateDTO;
import com.devaneios.turmadeelite.dto.AchievementPanelDTO;
import com.devaneios.turmadeelite.entities.Achievement;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AchievementService {
    void createAchievement(AchievementCreateDTO achievementCreateDTO, String teacherAuthUuid);
    Page<Achievement> getAchievements(int size, int pageNumber, String teacherAuthUuid);
    Optional<Achievement> findAchievementById(Long achievementId);
    void updateAchievement(Long achievementId, AchievementCreateDTO achievementCreateDTO,String teacherAuthUuid);
    List<AchievementPanelDTO> getStudentAchievements(String studentAuthUuid);
}

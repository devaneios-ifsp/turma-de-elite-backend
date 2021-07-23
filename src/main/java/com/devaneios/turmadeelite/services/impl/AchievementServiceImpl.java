package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.AchievementCreateDTO;
import com.devaneios.turmadeelite.entities.Achievement;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.repositories.AchievementRepository;
import com.devaneios.turmadeelite.repositories.ActivityRepository;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.services.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public void createAchievement(AchievementCreateDTO achievementCreateDTO, String teacherAuthUuid) {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        Achievement achievement = achievementCreateDTO.toEntity(teacher);
        this.achievementRepository.save(achievement);
    }

    @Override
    public Page<Achievement> getAchievements(int size, int pageNumber, String teacherAuthUuid) {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        Pageable page = PageRequest.of(pageNumber,size);
        return this.achievementRepository.findByTeacherId(teacher.getId(),page);
    }

    @Override
    public Optional<Achievement> findAchievementById(Long achievementId) {
        return this.achievementRepository.findById(achievementId);
    }

    @Override
    public void updateAchievement(Long achievementId, AchievementCreateDTO achievementCreateDTO, String teacherAuthUuid) {
        this.achievementRepository.findById(achievementId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        Achievement achievement = achievementCreateDTO.toEntity(teacher);
        achievement.setId(achievementId);
        this.achievementRepository.save(achievement);
    }
}

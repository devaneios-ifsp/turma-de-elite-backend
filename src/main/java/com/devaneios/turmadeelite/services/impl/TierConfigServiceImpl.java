package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.TierConfigDTO;
import com.devaneios.turmadeelite.entities.SchoolClass;
import com.devaneios.turmadeelite.entities.TierConfig;
import com.devaneios.turmadeelite.repositories.SchoolClassRepository;
import com.devaneios.turmadeelite.repositories.TierConfigRepository;
import com.devaneios.turmadeelite.services.TierConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@Service
public class TierConfigServiceImpl implements TierConfigService {

    private final TierConfigRepository tierConfigRepository;
    private final SchoolClassRepository schoolClassRepository;

    @Override
    public void saveTierConfig(TierConfigDTO configDTO, Long schoolClassId) {
        SchoolClass schoolClass = this.schoolClassRepository
                .findById(schoolClassId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        TierConfig tierConfig = TierConfig
                .builder()
                .schoolClass(schoolClass)
                .goldPercent(configDTO.getGoldPercent())
                .silverPercent(configDTO.getSilverPercent())
                .bronzePercent(configDTO.getBronzePercent())
                .build();

        schoolClass.setTierConfig(tierConfig);
        this.schoolClassRepository.save(schoolClass);
    }

    @Override
    public void updateTierConfig(TierConfigDTO tierConfigDTO, Long classId) {
        SchoolClass schoolClass = this.schoolClassRepository
                .findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        TierConfig tierConfig = this.tierConfigRepository
                .findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        tierConfig.setGoldPercent(tierConfigDTO.getGoldPercent());
        tierConfig.setSilverPercent(tierConfigDTO.getSilverPercent());
        tierConfig.setBronzePercent(tierConfigDTO.getBronzePercent());
        tierConfig.setSchoolClass(schoolClass);

        this.tierConfigRepository.save(tierConfig);
    }
}

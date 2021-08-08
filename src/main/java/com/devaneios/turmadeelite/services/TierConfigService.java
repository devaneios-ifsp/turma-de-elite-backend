package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.TierConfigDTO;
import com.devaneios.turmadeelite.entities.TierConfig;

public interface TierConfigService {
    void saveTierConfig(TierConfigDTO tierConfig, Long schoolClassId);

    void updateTierConfig(TierConfigDTO tierConfigDTO, Long classId);
}

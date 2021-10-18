package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.TierConfigDTO;

public interface TierConfigService {
    void saveTierConfig(TierConfigDTO tierConfig, Long schoolClassId);

    void updateTierConfig(TierConfigDTO tierConfigDTO, Long classId);
}

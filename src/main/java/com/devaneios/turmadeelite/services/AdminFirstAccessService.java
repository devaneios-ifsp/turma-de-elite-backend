package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;

public interface AdminFirstAccessService {
    void doFirstAccess(FirstAccessDTO firstAccessDTO) throws Exception;
    String verifyToken(String verifyToken);
}

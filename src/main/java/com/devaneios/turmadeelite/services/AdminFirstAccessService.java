package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.exceptions.UserNotFoundException;

public interface AdminFirstAccessService {
    void doFirstAccess(FirstAccessDTO firstAccessDTO) throws Exception;
    void verifyToken(String verifyToken);
}

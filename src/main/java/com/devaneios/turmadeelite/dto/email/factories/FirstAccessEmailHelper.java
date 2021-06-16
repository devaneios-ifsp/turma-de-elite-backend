package com.devaneios.turmadeelite.dto.email.factories;

import com.devaneios.turmadeelite.dto.email.english.PortugueseFirstAccessEmailDTO;
import com.devaneios.turmadeelite.dto.email.FirstAccessEmailDTO;
import com.devaneios.turmadeelite.dto.Language;
import com.devaneios.turmadeelite.dto.email.portugues.EnglishFirstAccessEmailDTO;

public class FirstAccessEmailHelper {
    public static FirstAccessEmailDTO createEmail(String to, String name, String link, Language language){
        switch (language){
            case pt:
                return new EnglishFirstAccessEmailDTO(to,name,link);
            case en:
                return new PortugueseFirstAccessEmailDTO(to,name,link);
            default:
                throw new IllegalArgumentException("É necessário providenciar um valor válido para language");
        }
    }
}

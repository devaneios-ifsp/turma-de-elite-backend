package com.devaneios.turmadeelite.events;

import com.devaneios.turmadeelite.dto.Language;
import com.devaneios.turmadeelite.entities.UserCredentials;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreated extends ApplicationEvent {

    private final UserCredentials userCredentials;
    private Language language;

    public UserCreated(Object source, UserCredentials userCredentials, String language) {
        super(source);
        this.userCredentials = userCredentials;
        switch (language){
            case "pt":
                this.language = Language.pt;
            case "en":
                this.language = Language.en;
        }
    }
}

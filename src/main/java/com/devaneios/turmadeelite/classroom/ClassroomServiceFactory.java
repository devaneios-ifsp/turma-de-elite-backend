package com.devaneios.turmadeelite.classroom;

import com.devaneios.turmadeelite.classroom.authentication.GoogleOauth2Service;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.classroom.Classroom;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.devaneios.turmadeelite.classroom.authentication.GoogleOauth2Service.HTTP_TRANSPORT;
import static com.devaneios.turmadeelite.classroom.authentication.GoogleOauth2Service.JSON_FACTORY;

@Service
@AllArgsConstructor
public class ClassroomServiceFactory {

    private final GoogleOauth2Service googleOauth2Service;

    public static final String APPLICATION_NAME = "Turma de Elite";

    public Classroom getService(String authUuid) throws IOException {
        Credential credential = this.googleOauth2Service.getCredential(authUuid);
        return new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY,credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}

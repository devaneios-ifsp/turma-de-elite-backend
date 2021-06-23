package com.devaneios.turmadeelite.listeners;

import com.devaneios.turmadeelite.dto.email.FirstAccessEmailDTO;
import com.devaneios.turmadeelite.dto.email.factories.FirstAccessEmailHelper;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.mail.MessagingException;

@Component
@AllArgsConstructor
public class SendFirstAccessEmailListener {

    private final JavaMailSender emailSender;

    @TransactionalEventListener
    public void sendEmailWhenUserCreated(UserCreated userCreated) throws MessagingException {
        UserCredentials credentials = userCreated.getUserCredentials();
        String link = new StringBuilder()
                .append("https://turma-de-elite-app.web.app/first-access?")
                .append("token=").append(credentials.getFirstAccessToken())
                .append("&email=").append(credentials.getEmail())
                .toString();
        FirstAccessEmailDTO email = FirstAccessEmailHelper.createEmail(
                credentials.getEmail(),
                credentials.getName(),
                link,
                userCreated.getLanguage()
        );
        email.sendMail(emailSender);
    }
}

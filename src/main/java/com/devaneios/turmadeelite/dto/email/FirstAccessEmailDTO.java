package com.devaneios.turmadeelite.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@AllArgsConstructor
@Getter
public abstract class FirstAccessEmailDTO {
    private final String to;
    private final String subject;
    private final String text;

    public void sendMail(JavaMailSender mailSender) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("turma-de-elite@gmail.com");
        helper.setTo(this.to);
        helper.setSubject(this.subject);
        helper.setText(this.text);
        mailSender.send(message);
    }
}

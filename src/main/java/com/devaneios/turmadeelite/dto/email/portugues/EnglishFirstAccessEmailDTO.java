package com.devaneios.turmadeelite.dto.email.portugues;

import com.devaneios.turmadeelite.dto.email.FirstAccessEmailDTO;

public class EnglishFirstAccessEmailDTO extends FirstAccessEmailDTO {

    private static final String subject = "[Turma de Elite] First Access";

    public EnglishFirstAccessEmailDTO(String to, String name, String link) {
        super(to, subject, makeText(name,link));
    }

    private static String makeText(String name, String link){
        return "Olá " + name + ", be welcome! You has been registered in the Turma de Elite platform. To access your space, do first access with the following steps:\n" +
                "Step 1: access the link " + link +"\n"+
                "Step 2: define the password with the fields \"Password\" and \"Confirm password\".\n" +
                "Step 3: click on \"Do first access\"\n" +
                "Step 4: Login inserting the e-mail and password inserted in first access\n" +
                "\n" +
                "The first access can do only one time, in case you have forgotten the password, you can recover it clicking on the button \"Forgot password\" in the login screen.\n" +
                "The first access link is personal and non-transferable, and expires as soon as the first access is confirmed. \n" +
                "\n" +
                "If you are not expecting this e-mail, please ignore it.\n" +
                "\n" +
                "Best regargs, admin team of Turma de Elite platform";
    }
}

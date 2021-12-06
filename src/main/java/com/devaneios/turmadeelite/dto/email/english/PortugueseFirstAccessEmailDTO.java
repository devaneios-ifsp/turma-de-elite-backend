package com.devaneios.turmadeelite.dto.email.english;

import com.devaneios.turmadeelite.dto.email.FirstAccessEmailDTO;

public class PortugueseFirstAccessEmailDTO extends FirstAccessEmailDTO {
    private static final String subject = "[Turma de Elite] Primeiro Acesso";

    public PortugueseFirstAccessEmailDTO(String to, String name, String link) {
        super(to, subject, makeText(name,link));
    }

    private static String makeText(String name, String link){
        return "Olá " + name + ", seja bem-vindo! Você foi cadastrado(a) na plataforma Turma de Elite. Para acessar seu ambiente, realize seu primeiro acesso com os seguintes passos:\n" +
                "Passo 1: acesse o link " + link + "\n" +
                "Passo 2: defina sua senha através dos campos \"Senha\" e \"Confirmar senha\".\n" +
                "Passo 3: clique em \"Realizar primeiro acesso\"\n" +
                "Passo 4: realize o login inserindo o e-mail e senha inseridos no primeiro acesso\n" +
                "\n" +
                "O primeiro acesso na aplicaçao só poderá ser realizado uma única vez, em caso de esquecimento de senha, você poderá recuperá-la utilizando o botão \"Esqueci a senha\" localizado na tela de login.\n" +
                " O link do primeiro acesso é pessoal e instransferível, e expirará assim que o primeiro acesso for confirmado. \n" +
                "\n" +
                "Se você não estava esperando este e-mail, por favor deconsidere-o\n" +
                "\n" +
                "Atenciosamente, equipe administrativa da plataforma Turma de Elite";
    }
}

package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class AdminAuthentication {

    @Autowired
    private MockMvc mvc;

    static ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Verificar token e realizar primeiro acesso, criando um usuário no sistema de autenticação externo")
    @Test
    void firstAccessFlow() throws Exception {
        FirstAccessDTO firstAccessDTO = new FirstAccessDTO("andre.montero702@gmail.com", "123456", "exemplo_token");
        mvc.perform(post("/first-access/verify-token")
                .content("exemplo_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mvc.perform(post("/first-access")
        .content(mapper.writeValueAsString(firstAccessDTO))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());

    }
}

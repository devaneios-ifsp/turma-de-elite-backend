package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.configuration.TestConfiguration;
import com.devaneios.turmadeelite.utils.FirebaseCredentialsFromEnv;
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

@Import({TestConfiguration.class})
@AutoConfigureMockMvc
@SpringBootTest
public class AdminAuthentication {

    @Autowired
    private MockMvc mvc;

    @MockBean
    FirebaseCredentialsFromEnv env;

    @DisplayName("Verificar token e realizar primeiro acesso, criando um usuário no sistema de autenticação exteno")
    @Test
    void firstAccessFlow() throws Exception {
        mvc.perform(post("/first-access/verify-token")
                .content("exemplo_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
}

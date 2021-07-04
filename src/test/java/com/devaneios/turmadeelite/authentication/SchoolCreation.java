package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.impl.FirebaseAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc()
public class SchoolCreation {

    @Autowired
    private MockMvc mvc;

    static String bearerToken;

    static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setup(
            @Autowired MockMvc mvc,
            @Autowired FirebaseAuthenticationService authenticationService,
            @Autowired UserRepository userRepository) throws Exception {
        if(bearerToken == null){
            String firstAccessToken = "esse_token";
            String email = "andre@aluno.ifsp.edu.br";
            String password = "123456";
            UserCredentials credentials = UserCredentials
                    .builder()
                    .email(email)
                    .firstAccessToken(firstAccessToken)
                    .name("André")
                    .role(Role.ADMIN)
                    .build();
            UserCredentials saved = userRepository.save(credentials);
            FirstAccessDTO firstAccessDTO = new FirstAccessDTO(email, password, firstAccessToken);

            mvc.perform(post("/first-access/verify-token")
                    .content(firstAccessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(result -> {
                        String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        Assertions.assertEquals(responseBody,email);
                    });

            mvc.perform(post("/first-access")
                    .content(mapper.writeValueAsString(firstAccessDTO))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());

            bearerToken = "Bearer " + authenticationService.createTokenFrom(email,password);
        }
    }

    @Test
    @DisplayName("Criar uma escola")
    void createSchool() throws Exception {
        SchoolCreateDTO school = SchoolCreateDTO.builder()
                .name("Batista Renzi")
                .identifier("Suzano - BR")
                .build();

        mvc.perform(post("/api/schools")
                .content(mapper.writeValueAsString(school))
                .header("Authorization",bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/schools?size=5&pageNumber=0")
                .content(mapper.writeValueAsString(school))
                .header("Authorization",bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

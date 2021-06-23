package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.AdminCreateDTO;
import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.repositories.AdminRepository;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import com.devaneios.turmadeelite.security.SecurityConfiguration;
import com.devaneios.turmadeelite.services.impl.FirebaseAuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc()
public class AdminAuthentication {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FirebaseAuthenticationService authenticationService;

    static ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Criar um usuário admin sem estar autenticado")
    @Test
    void creatingAdminUnauthenticated() throws Exception {
        AdminCreateDTO dto = new AdminCreateDTO("patricia.paschoal@aluno.ifsp.edu.br", "Patrícia Paschoal", "pt");
        mvc.perform(post("/api/admin")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Criar um usuário admin estando autenticado com a Role correta e verificar se ele é retornado corretamente")
    @Test
    void creatingAdminWithRightRole() throws Exception {
        UserCredentials saved = this.adminRepository.save(new UserCredentials(null, "bianca@aluno.ifsp.edu.br", null, "outro_token", "Patrícia Paschoal", Role.ADMIN));
        FirstAccessDTO firstAccessDTO = new FirstAccessDTO(
                "bianca@aluno.ifsp.edu.br",
                "123456",
                "outro_token");

        mvc.perform(post("/first-access/verify-token")
                .content("outro_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Assertions.assertEquals(responseBody,"bianca@aluno.ifsp.edu.br");
                });

        mvc.perform(post("/first-access")
                .content(mapper.writeValueAsString(firstAccessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        UserCredentials byId = this.adminRepository.findById(saved.getId()).get();
        String token = this.authenticationService.createTokenFrom("bianca@aluno.ifsp.edu.br","123456");
        AdminCreateDTO dto = new AdminCreateDTO("luis@aluno.ifsp.edu.br", "Luis ", "pt");
        mvc.perform(post("/api/admin")
                .content(mapper.writeValueAsString(dto))
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Verificar token e realizar primeiro acesso, criando um usuário no sistema de autenticação externo")
    @Test
    void firstAccessFlow() throws Exception {
        FirstAccessDTO firstAccessDTO = new FirstAccessDTO(
                "andre.montero702@gmail.com",
                "123456",
                "exemplo_token");

        mvc.perform(post("/first-access/verify-token")
                .content("exemplo_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    Assertions.assertEquals(responseBody,"andre.montero702@gmail.com");
                });

        mvc.perform(post("/first-access")
        .content(mapper.writeValueAsString(firstAccessDTO))
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
    }

    @DisplayName("Não é possível realizar primeiro acesso duas vezes")
    @Test
    void conflictFailFirstAccess() throws Exception{

        FirstAccessDTO firstAccessDTO = new FirstAccessDTO(
                "natan.lisboa@aluno.ifsp.edu.br",
                "123456",
                "token_repetido");

        mvc.perform(post("/first-access")
                .content(mapper.writeValueAsString(firstAccessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(post("/first-access/verify-token")
                .content("token_repetido")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        mvc.perform(post("/first-access")
                .content(mapper.writeValueAsString(firstAccessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

    }

    @DisplayName("Não é possível realizar primeiro acesso de alguém que nunca se cadastrou")
    @Test
    void notFoundFail() throws Exception{
        FirstAccessDTO firstAccessDTO = new FirstAccessDTO(
                "natan.lisboa@gmail.com.br",
                "123456",
                "um_token_inexistente");

        mvc.perform(post("/first-access/verify-token")
                .content("um_token_inexistente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mvc.perform(post("/first-access")
                .content(mapper.writeValueAsString(firstAccessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}

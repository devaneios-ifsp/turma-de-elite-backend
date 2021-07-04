package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.impl.FirebaseAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

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
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuthenticationService authenticationService;

    static ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Criar um usuário admin sem estar autenticado")
    @Test
    void creatingAdminUnauthenticated() throws Exception {
        UserCredentialsCreateDTO dto = new UserCredentialsCreateDTO("patricia.paschoal@aluno.ifsp.edu.br", "Patrícia Paschoal", "pt");
        mvc.perform(post("/api/admin")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Recuperar role sem estar autenticado")
    @Test
    void recoverRoleUnauthorized() throws Exception {
        mvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Fluxo completo da criação de um usuário")
    @Test
    void creatingAdminWithRightRole() throws Exception {
        UserCredentials saved = this.userRepository.save(new UserCredentials(null, "bianca@aluno.ifsp.edu.br", null, "outro_token", "Patrícia Paschoal", Role.ADMIN));
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

        String token = this.authenticationService.createTokenFrom("bianca@aluno.ifsp.edu.br","123456");

        mvc.perform(get("/api/roles")
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String role = response.getContentAsString();
                    Assertions.assertEquals("ADMIN",role);
                });

        UserCredentialsCreateDTO dto = new UserCredentialsCreateDTO("luis@aluno.ifsp.edu.br", "Luis ", "pt");
        mvc.perform(post("/api/admin")
                .content(mapper.writeValueAsString(dto))
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/admin?size=5&pageNumber=0")
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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

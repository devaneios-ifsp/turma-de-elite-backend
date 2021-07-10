package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.authentication.utils.MockPage;
import com.devaneios.turmadeelite.dto.AdminViewDTO;
import com.devaneios.turmadeelite.dto.SchoolViewDTO;
import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AdminCRUDTestHelper extends CRUDTestHelper<UserCredentialsCreateDTO, AdminViewDTO> {

    public AdminCRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token) {
        super(mvc, mapper, token, "/admin");
    }

    @Override
    public List<UserCredentialsCreateDTO> buildCreateDTOs() {
        UserCredentialsCreateDTO user1 = UserCredentialsCreateDTO
                .builder()
                .email("joao.eufrasino@gmail.com")
                .name("João Eufrasino")
                .isActive(true)
                .language("pt")
                .build();

        UserCredentialsCreateDTO user2 = UserCredentialsCreateDTO
                .builder()
                .email("jose.bonifacio@gmail.com")
                .name("José Bonifácio")
                .isActive(true)
                .language("pt")
                .build();
        return Arrays.asList(user1,user2);
    }

    @Override
    protected List<AdminViewDTO> parseEntities(String raw) throws Exception {
        MockPage<List<AdminViewDTO>> entityPage = mapper.readValue(raw,new TypeReference<MockPage<List<AdminViewDTO>>>(){});
        return entityPage.content;
    }

    @Override
    protected UserCredentialsCreateDTO changeValues(AdminViewDTO adminViewDTO) {
        return UserCredentialsCreateDTO
                .builder()
                .email(adminViewDTO.email)
                .name(adminViewDTO.getName().toUpperCase(Locale.ROOT))
                .isActive(false)
                .language("pt")
                .build();
    }
}

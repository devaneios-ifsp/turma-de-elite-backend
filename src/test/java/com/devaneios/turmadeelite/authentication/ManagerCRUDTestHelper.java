package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.authentication.utils.MockPage;
import com.devaneios.turmadeelite.dto.AdminViewDTO;
import com.devaneios.turmadeelite.dto.SchoolUserCreateDTO;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ManagerCRUDTestHelper extends CRUDTestHelper<SchoolUserCreateDTO, SchoolUserViewDTO> {
    public ManagerCRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token) {
        super(mvc, mapper, token, "/managers");
    }

    @Override
    public List<SchoolUserCreateDTO> buildCreateDTOs() {
        SchoolUserCreateDTO manager1 = SchoolUserCreateDTO
                .builder()
                .email("w.seymour.skinner@springfield.edu.br")
                .name("W. Seymour Skinner")
                .isActive(true)
                .language("pt")
                .build();

        SchoolUserCreateDTO manager2 = SchoolUserCreateDTO
                .builder()
                .email("albus.dumbledore@hogwarts.edu.br")
                .name("Albus Dumbledore")
                .isActive(true)
                .language("pt")
                .build();
        return Arrays.asList(manager1,manager2);
    }

    @Override
    protected List<SchoolUserViewDTO> parseEntities(String raw) throws Exception {
        MockPage<List<SchoolUserViewDTO>> entityPage = mapper.readValue(raw,new TypeReference<MockPage<List<SchoolUserViewDTO>>>(){});
        return entityPage.content;
    }

    @Override
    protected SchoolUserCreateDTO changeValues(SchoolUserViewDTO viewDTO) {
        return SchoolUserCreateDTO
                .builder()
                .name(viewDTO.name.toUpperCase(Locale.ROOT))
                .schoolId(viewDTO.getSchool().id)
                .email(viewDTO.email)
                .language("pt")
                .isActive(true)
                .build();
    }
}

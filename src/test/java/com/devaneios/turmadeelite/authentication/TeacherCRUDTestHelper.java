package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.authentication.utils.MockPage;
import com.devaneios.turmadeelite.dto.TeacherCreateDTO;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TeacherCRUDTestHelper extends CRUDTestHelper<TeacherCreateDTO, SchoolUserViewDTO> {
    public TeacherCRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token) {
        super(mvc, mapper, token, "/teachers");
    }

    @Override
    public List<TeacherCreateDTO> buildCreateDTOs() {
        TeacherCreateDTO teacher1 = TeacherCreateDTO
                .builder()
                .email("edna.krabappel@springfield.edu.br")
                .name("Edna Krabappel")
                .isActive(true)
                .language("pt")
                .build();

        TeacherCreateDTO teacher2 = TeacherCreateDTO
                .builder()
                .email("severus.snape@hogwarts.edu.br")
                .name("Severus Snape")
                .isActive(true)
                .language("pt")
                .build();
        return Arrays.asList(teacher1,teacher2);
    }

    @Override
    protected List<SchoolUserViewDTO> parseEntities(String raw) throws Exception {
        MockPage<List<SchoolUserViewDTO>> entityPage = mapper.readValue(raw,new TypeReference<MockPage<List<SchoolUserViewDTO>>>(){});
        return entityPage.content;
    }

    @Override
    protected TeacherCreateDTO changeValues(SchoolUserViewDTO viewDTO) {
        return TeacherCreateDTO
                .builder()
                .name(viewDTO.name.toUpperCase(Locale.ROOT))
                .email(viewDTO.email)
                .language("pt")
                .isActive(true)
                .build();
    }
}

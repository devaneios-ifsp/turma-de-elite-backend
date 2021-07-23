package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.authentication.utils.MockPage;
import com.devaneios.turmadeelite.dto.SchoolUserViewDTO;
import com.devaneios.turmadeelite.dto.StudentCreateDTO;
import com.devaneios.turmadeelite.dto.StudentViewDTO;
import com.devaneios.turmadeelite.entities.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

public class StudentCRUDTestHelper extends CRUDTestHelper<StudentCreateDTO, StudentViewDTO> {

    public StudentCRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token) {
        super(mvc, mapper, token, "/students");
    }

    @Override
    public List<StudentCreateDTO> buildCreateDTOs() {
        StudentCreateDTO aluno1 = new StudentCreateDTO("aluno1@gmail.com", "Aluno 1", "12345", true, "pt");
        StudentCreateDTO aluno2 = new StudentCreateDTO("aluno2@gmail.com", "Aluno 2", "22345", true, "pt");
        StudentCreateDTO aluno3 = new StudentCreateDTO("aluno3@gmail.com", "Aluno 3", "32345", true, "pt");
        return Arrays.asList(aluno1,aluno2,aluno3);
    }

    @Override
    protected List<StudentViewDTO> parseEntities(String raw) throws Exception {
        MockPage<List<StudentViewDTO>> entityPage = mapper.readValue(raw,new TypeReference<MockPage<List<StudentViewDTO>>>(){});
        return entityPage.content;
    }

    @Override
    protected StudentCreateDTO changeValues(StudentViewDTO viewDTO) {
        return new StudentCreateDTO(viewDTO.email, viewDTO.getName().toUpperCase(), viewDTO.registry, true, "pt");
    }
}

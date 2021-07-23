package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.authentication.utils.MockPage;
import com.devaneios.turmadeelite.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class ClassCRUDTestHelper extends CRUDTestHelper<ClassCreateDTO, SchoolClassViewDTO> {

    public ClassCRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token) {
        super(mvc, mapper, token, "/class");
    }

    @Override
    public List<ClassCreateDTO> buildCreateDTOs() {
        ClassCreateDTO mata1 = new ClassCreateDTO(null, null, "MATA1", true);
        ClassCreateDTO sida5 = new ClassCreateDTO(null, null, "SIDA5", true);
        ClassCreateDTO lg1A1 = new ClassCreateDTO(null, null, "LG1A1", true);
        return Arrays.asList(mata1,sida5,lg1A1);
    }

    @Override
    protected List<SchoolClassViewDTO> parseEntities(String raw) throws Exception {
        MockPage<List<SchoolClassViewDTO>> entityPage = mapper.readValue(raw,new TypeReference<MockPage<List<SchoolClassViewDTO>>>(){});
        return entityPage.content;
    }

    @Override
    protected ClassCreateDTO changeValues(SchoolClassViewDTO viewDTO) {
        List<Long> teachersId = viewDTO
                .getTeachers()
                .stream()
                .map(TeacherMembershipDTO::getTeacher)
                .map(SchoolUserViewDTO::getId)
                .collect(Collectors.toList());

        List<Long> studentsId = viewDTO
                .getStudents()
                .stream()
                .map(StudentMembershipDTO::getStudent)
                .map(StudentViewDTO::getId)
                .collect(Collectors.toList());

        return new ClassCreateDTO(teachersId, studentsId, viewDTO.getName().toLowerCase(), true);
    }

}

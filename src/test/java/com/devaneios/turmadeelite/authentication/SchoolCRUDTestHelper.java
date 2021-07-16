package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.authentication.utils.MockPage;
import com.devaneios.turmadeelite.authentication.utils.SchoolPage;
import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.dto.SchoolCreateDTO;
import com.devaneios.turmadeelite.dto.SchoolViewDTO;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.repositories.SchoolRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.SchoolService;
import com.devaneios.turmadeelite.services.impl.FirebaseAuthenticationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SchoolCRUDTestHelper extends CRUDTestHelper<SchoolCreateDTO,SchoolViewDTO>{

    public SchoolCRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token){
        super(mvc,mapper,token,"/schools");
    }

    public List<SchoolCreateDTO> buildCreateDTOs(){
        SchoolCreateDTO batistaRenzi = SchoolCreateDTO.builder()
                .name("Batista Renzi")
                .identifier("Suzano - BR")
                .isActive(true)
                .build();

        SchoolCreateDTO luizaHidaka = SchoolCreateDTO.builder()
                .name("Luíza Hidaka")
                .identifier("Suzano - LH")
                .isActive(true)
                .build();

        return Arrays.asList(batistaRenzi,luizaHidaka);
    }

    public List<SchoolViewDTO> parseEntities(String raw) throws Exception{
        MockPage<List<SchoolViewDTO>> entityPage = mapper.readValue(raw,new TypeReference<MockPage<List<SchoolViewDTO>>>(){});
        return entityPage.content;
    }

    protected SchoolCreateDTO changeValues(SchoolViewDTO schoolDTO){
        String upperCaseName = schoolDTO.getName().toUpperCase(Locale.ROOT);
        return SchoolCreateDTO.builder()
                .name(upperCaseName)
                .identifier(schoolDTO.getIdentifier())
                .isActive(schoolDTO.getIsActive())
                .build();
    }
}

package com.devaneios.turmadeelite.authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class CRUDTestHelper<CreateType, ViewType> {
    protected MockMvc mvc;
    protected ObjectMapper mapper;
    protected String bearerToken;
    protected String path;

    public CRUDTestHelper(MockMvc mvc, ObjectMapper mapper, String token, String pathPrefix){
        this.mvc = mvc;
        this.mapper = mapper;
        this.bearerToken = "Bearer " + token;
        this.path = "/api" + pathPrefix;
    }

    public abstract List<CreateType> buildCreateDTOs();

    void createSomeEntities() throws Exception{
        List<CreateType> dtos = this.buildCreateDTOs();
        for(CreateType dto: dtos){
            postEntity(dto);
        }
    }

    public void postEntity(CreateType entity) throws Exception{
        mvc.perform(post(this.path)
                .content(mapper.writeValueAsString(entity))
                .header("Authorization",bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    public void getByNameSimilarity() throws Exception {
        mvc.perform(get(path +"/name/bat")
                .header("Authorization",this.bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    String rawResponse = result.getResponse().getContentAsString();
                    List<ViewType> schools = mapper.readValue(rawResponse, new TypeReference<List<ViewType>>(){});
                    Assertions.assertTrue(schools.size() == 1);
                });
    }

    public List<ViewType> listEntities() throws Exception {
        MvcResult result = mvc.perform(get(path + "?size=5&pageNumber=0")
                .header("Authorization", bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String raw = result.getResponse().getContentAsString();
        return parseEntities(raw);
    }

    protected abstract List<ViewType> parseEntities(String raw) throws Exception;

    public void getById(Long id) throws Exception{
        mvc.perform(get(this.path+"/" + id)
                .header("Authorization",this.bearerToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    protected abstract CreateType changeValues(ViewType viewDTO);

    public void updateEntity(Long id, ViewType createType) throws Exception {
        mvc.perform(put(this.path+"/" + id)
                .header("Authorization",this.bearerToken)
                .content(mapper.writeValueAsString(changeValues(createType)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

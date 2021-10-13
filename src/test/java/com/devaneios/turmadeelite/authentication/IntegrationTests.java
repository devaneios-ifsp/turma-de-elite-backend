package com.devaneios.turmadeelite.authentication;

import com.devaneios.turmadeelite.dto.*;
import com.devaneios.turmadeelite.entities.Attachment;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.DataStorageService;
import com.devaneios.turmadeelite.services.impl.FirebaseAuthenticationService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc()
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class IntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FirebaseAuthenticationService authenticationService;

    @Autowired
    private DataStorageService dataStorageService;

    static ObjectMapper mapper = new ObjectMapper();

    static String token = "";

    static SchoolViewDTO firstSchool = null;
    static SchoolUserViewDTO firstManager = null;
    static String teacherToken;
    static String studentToken;
    static String managerToken;

    @BeforeAll
    static void setup(
            @Autowired MockMvc mvc,
            @Autowired UserRepository userRepository,
            @Autowired FirebaseAuthenticationService authenticationService) throws Exception{
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserCredentials user = UserCredentials
                .builder()
                .email("bianca@aluno.ifsp.edu.br")
                .firstAccessToken("outro_token")
                .name("Bianca")
                .isActive(true)
                .role(Role.ADMIN)
                .build();
        UserCredentials saved = userRepository.save(user);
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

        token = authenticationService.createTokenFrom("bianca@aluno.ifsp.edu.br","123456");

    }

    @DisplayName("Criar um usuário admin sem estar autenticado")
    @Test
    @Order(1)
    void creatingAdminUnauthenticated() throws Exception {
        UserCredentialsCreateDTO dto = new UserCredentialsCreateDTO("patricia.paschoal@aluno.ifsp.edu.br", "Patrícia Paschoal",true, "pt");
        mvc.perform(post("/api/admin")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Recuperar role sem estar autenticado")
    @Test
    @Order(2)
    void recoverRoleUnauthorized() throws Exception {
        mvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Fluxo completo da criação de um usuário")
    @Test
    @Order(3)
    void creatingAdminWithRightRole() throws Exception {
        mvc.perform(get("/api/roles")
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    String role = response.getContentAsString();
                    Assertions.assertEquals("ADMIN",role);
                });

        UserCredentialsCreateDTO dto = new UserCredentialsCreateDTO("luis@aluno.ifsp.edu.br", "Luis ", true,"pt");
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
    @Order(3)
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
    @Order(3)
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
    @Order(4)
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

    @DisplayName("Recuperar administradores pelo Id e atualizá-los")
    @Test
    @Order(5)
    void listAndUpdateAdmin() throws Exception{
        AdminCRUDTestHelper adminTestHelper = new AdminCRUDTestHelper(mvc, mapper, token);
        adminTestHelper.createSomeEntities();
        List<AdminViewDTO> admins = adminTestHelper.listEntities();
        System.out.println(admins.get(0));
        for(AdminViewDTO admin: admins){
            adminTestHelper.getById(admin.getId());
            adminTestHelper.updateEntity(admin.getId(),admin);
        }
    }

    @DisplayName("Criar,Atualizar e listar escolas")
    @Test
    @Order(6)
    void createSchool() throws Exception {
        SchoolCRUDTestHelper schoolHelper = new SchoolCRUDTestHelper(mvc, mapper, token);
        schoolHelper.createSomeEntities();
        schoolHelper.getByNameSimilarity();
        List<SchoolViewDTO> schools = schoolHelper.listEntities();
        for (SchoolViewDTO school : schools) {
            schoolHelper.getById(school.id);
            schoolHelper.updateEntity(school.id, school);
            if (firstSchool == null) {
                firstSchool = school;
            }
        }
    }

    @DisplayName("Criar,Atualizar e listar gestores")
    @Test
    @Order(7)
    void createManager() throws Exception {

        ManagerCRUDTestHelper managerTestHelper = new ManagerCRUDTestHelper(mvc, mapper, token);

        List<ManagerCreateDTO> managers = managerTestHelper.buildCreateDTOs();
        for (ManagerCreateDTO manager : managers) {
            manager.setSchoolId(firstSchool.id);
            managerTestHelper.postEntity(manager);
        }

        List<SchoolUserViewDTO> registeredManagers = managerTestHelper.listEntities();
        for (SchoolUserViewDTO manager : registeredManagers) {
            System.out.println(registeredManagers);
            if (firstManager == null) firstManager = manager;
            managerTestHelper.getById(manager.getId());
            managerTestHelper.updateEntity(manager.id, manager);
        }
    }

    @DisplayName("Realizar primeiro acesso do gestor e criar uma turma")
    @Test
    @Order(8)
    void doManagerFirstAccess() throws Exception {

        UserCredentials user = this.userRepository
                .findById(firstManager.getId())
                .orElseThrow(Exception::new);

        FirstAccessDTO firstAccessDTO = new FirstAccessDTO(
                user.getEmail(),
                "123456",
                user.getFirstAccessToken());

        mvc.perform(post("/first-access")
                .content(mapper.writeValueAsString(firstAccessDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        managerToken = this.authenticationService.createTokenFrom(user.getEmail(), "123456");
        TeacherCRUDTestHelper teacherTestHelper = new TeacherCRUDTestHelper(mvc, mapper, managerToken);
        List<TeacherCreateDTO> teachers = teacherTestHelper.buildCreateDTOs();

        for (TeacherCreateDTO teacher : teachers) {
            teacherTestHelper.postEntity(teacher);
        }

        List<SchoolUserViewDTO> registeredTeachers = teacherTestHelper.listEntities();
        for (SchoolUserViewDTO teacher : registeredTeachers) {
            teacherTestHelper.getById(teacher.getId());
            teacherTestHelper.updateEntity(teacher.getId(), teacher);
        }

        StudentCRUDTestHelper studentCRUDTestHelper = new StudentCRUDTestHelper(mvc, mapper, managerToken);
        List<StudentCreateDTO> students = studentCRUDTestHelper.buildCreateDTOs();

        for (StudentCreateDTO student : students) {
            studentCRUDTestHelper.postEntity(student);
        }

        List<StudentViewDTO> studentsListed = studentCRUDTestHelper.listEntities();
        for (StudentViewDTO studentListed : studentsListed) {
            studentCRUDTestHelper.getById(studentListed.id);
            studentCRUDTestHelper.updateEntity(studentListed.id, studentListed);
        }

        ClassCRUDTestHelper classCRUDTestHelper = new ClassCRUDTestHelper(mvc, mapper, managerToken);
        List<ClassCreateDTO> classes = classCRUDTestHelper.buildCreateDTOs();

        List<Long> teachersId = registeredTeachers
                .stream()
                .map(SchoolUserViewDTO::getId)
                .collect(Collectors.toList());

        List<Long> studentsId = studentsListed
                .stream()
                .map(StudentViewDTO::getId)
                .collect(Collectors.toList());

        for (ClassCreateDTO schoolClass : classes) {
            schoolClass.setTeachersId(teachersId);
            schoolClass.setStudentsId(studentsId);
            classCRUDTestHelper.postEntity(schoolClass);
        }

    }

    @DisplayName("Realizar alterações na turma, como remoção de alunos")
    @Test
    @Order(9)
    void classUpdate() throws Exception{
        ClassCRUDTestHelper classCRUDTestHelper = new ClassCRUDTestHelper(mvc, mapper, managerToken);

        List<SchoolClassViewDTO> classesListed = classCRUDTestHelper.listEntities();
        for(SchoolClassViewDTO classListed:classesListed){
            classCRUDTestHelper.getById(classListed.getId());
            classCRUDTestHelper.updateEntity(classListed.getId(), classListed);
        }


//        List<Long> classIds = classesListed
//                .stream()
//                .map(SchoolClassViewDTO::getId)
//                .collect(Collectors.toList());
//
//        SchoolClassViewDTO firstSchoolClass = classesListed.get(0);
//        StudentViewDTO firstStudent = studentsListed.get(0);
//        SchoolUserViewDTO firstTeacher = registeredTeachers.get(0);
//
//        mvc.perform(put("/api/class/" + firstSchoolClass.getId() +"/student/" + firstStudent.getId())
//                .header("Authorization","Bearer "+ tokenFrom)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("false"))
//                .andExpect(status().isOk());
//
//        mvc.perform(put("/api/class/" + firstSchoolClass.getId() +"/teacher/" + firstStudent.getId())
//                .header("Authorization","Bearer "+ tokenFrom)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("false"))
//                .andExpect(status().isOk());
//
//        teacherTestHelper.postEntity(new TeacherCreateDTO("novo-professor@gmail.com","Novo Professor",true,"pt"));
//
//        List<SchoolUserViewDTO> newTeachers = teacherTestHelper.listEntities();
//        for(SchoolUserViewDTO teacher: newTeachers){
//            if(teacher.email.equals("novo-professor@gmail.com")){
//                mvc.perform(post("/api/class/" + firstSchoolClass.getId() + "/teacher/" + teacher.getId())
//                        .header("Authorization","Bearer "+ tokenFrom)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("false"))
//                        .andExpect(status().isCreated());
//            }
//        }
//
//        teacherToken = getTokenFromUser(firstTeacher.getId());
//        studentToken = getTokenFromUser(firstStudent.getId());
    }
//
//    @DisplayName("Criar, listar e atualizar atividades")
//    @Test
//    @Order(8)
//    void createActivities()throws Exception{
//        ActivitiesTestHelper activitiesTestHelper = new ActivitiesTestHelper(mvc, mapper, teacherToken, studentToken);
//        List<ActivityCreateDTO> activities = activitiesTestHelper.createActivities();
//        activitiesTestHelper.saveActivities(activities);
//        activitiesTestHelper.updateActivity(1L);
//        activitiesTestHelper.activitiesCanBeListedPaginated();
//        activitiesTestHelper.activitiesCanBeListed();
//        activitiesTestHelper.getStudentActivities();
//    }
//
//    @DisplayName("Entregar atividades")
//    @Test
//    @Order(9)
//    void deliveryActivities()throws Exception{
//        ActivityDeliveryTestHelper activityDeliveryTestHelper = new ActivityDeliveryTestHelper(
//                mvc,
//                mapper,
//                studentToken,
//                teacherToken);
//
//        MockMultipartFile multipartFile = new MockMultipartFile("file", "fis".getBytes());
//
//        activityDeliveryTestHelper.deliveryActivity(1L);
//    }
//
    private String getTokenFromUser(Long id) throws Exception {
        UserCredentials user = this.userRepository
                .findById(id)
                .orElseThrow(Exception::new);

        FirstAccessDTO teacherFirstAccess = new FirstAccessDTO(
                user.getEmail(),
                "123456",
                user.getFirstAccessToken());

        mvc.perform(post("/first-access")
                .content(mapper.writeValueAsString(teacherFirstAccess))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        return this.authenticationService.createTokenFrom(user.getEmail(), "123456");
    }


}

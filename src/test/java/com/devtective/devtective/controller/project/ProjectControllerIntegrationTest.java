package com.devtective.devtective.controller.project;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.worker.WorkerRequestDTO;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.repository.WorkerRepository;
import com.devtective.devtective.service.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class ProjectControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private WorkerRepository workerRepository;
    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private Long workerId;

    private final String username = "test" + new Random().nextInt(1_000_000);
    private final String email = username + "@example.com";

    private Cookie jwtCookie;

    public static Integer generateRandomBigNumber() {
        return (100000 + new Random().nextInt(900000));
    }
    public static String generateRandomName() {
        return "Project" + generateRandomBigNumber();
    }

    private Long projectId;

    @BeforeEach
    void setup() throws Exception {
        // Register user
        UserRequestDTO userDto = new UserRequestDTO(username, email, "password", Long.valueOf(1));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        // Login user
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = loginResult.getResponse();
        jwtCookie = response.getCookie("jwt");

        JsonNode loginJson = objectMapper.readTree(response.getContentAsString());
        userId = loginJson.get("userId").asLong();

        long posId = 1; //developer
        WorkerRequestDTO workerDTO = new WorkerRequestDTO(generateRandomBigNumber().longValue(), "Doctor", "Who", posId, userId);
        MvcResult workerCreateRes = mockMvc.perform(post("/api/v1/workers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(jwtCookie)
                        .content(objectMapper.writeValueAsString(workerDTO)))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse workerRes = workerCreateRes.getResponse();
        JsonNode createWorkerJson = objectMapper.readTree(workerRes.getContentAsString());
        workerId = createWorkerJson.get("id").asLong();
    }

    @AfterEach
    @Transactional
    void cleanup() {
        if (projectId != null) {
            projectRepository.deleteById(projectId);
        }
        if (workerId != null) {
            workerRepository.deleteById(workerId);
        }
        if (username != null) {
            userRepository.deleteByUsername(username);
        }
    }

    @Test
    void shouldCreateGetUpdateAndDeleteProject() throws Exception {
        String projectName = generateRandomName();
        projectId = generateRandomBigNumber().longValue();

        ProjectRequestDTO projectDto = new ProjectRequestDTO(projectId, projectName, "Initial description", "",
                                            LocalDate.now(), LocalDate.now(), workerId);

        // 1. Create a project
        String json = objectMapper.writeValueAsString(projectDto);
        String responseJson = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(jwtCookie)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectName))
                .andReturn().getResponse().getContentAsString();

        Project createdProject = objectMapper.readValue(responseJson, Project.class);
        projectId = createdProject.getId();

        // 2. Get project by ID
        mockMvc.perform(get("/api/v1/projects/" + projectId)
                .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectName))
                .andExpect(jsonPath("$.description").value("Initial description"));

        // 3. Get all projects (must include the one we created)
        mockMvc.perform(get("/api/v1/projects")
                .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(not(empty()))))
                .andExpect(jsonPath(String.format("$[?(@.id == %d)]", projectId)).exists());

        // 4. Update project
        ProjectRequestDTO updatedDto = new ProjectRequestDTO(projectId, projectName, "Updated description", "", LocalDate.now(), LocalDate.now(), workerId);
        mockMvc.perform(put("/api/v1/projects")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated description"));

        // 5. Delete project
        mockMvc.perform(delete("/api/v1/projects/" + projectId)
                .cookie(jwtCookie))
                .andExpect(status().isNoContent());
    }
}

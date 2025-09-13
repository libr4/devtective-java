package com.devtective.devtective.controller.project;

import com.devtective.devtective.controller.AbstractIntegrationTest;
import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectTaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private Cookie jwtCookie;
    private Long userId;
    private Long workerId;
    private Long projectId;

    // ------------ Utilities ------------

    private static Integer generateRandomBigNumber() {
        return 100000 + new Random().nextInt(900000);
    }
    private static String generateRandomName() {
        return "Task" + generateRandomBigNumber();
    }

    // ------------ Common Setup per test ------------

    @BeforeEach
    void setup() throws Exception {
        registerUserAndLogin();
        createWorkerForUser();
        projectId = createProject("Project" + generateRandomBigNumber(), "Initial description");
    }

    private void registerUserAndLogin() throws Exception {
        String username = "test" + generateRandomBigNumber();
        String email = username + "@example.com";
        UserRequestDTO userDto = new UserRequestDTO(username, email, "password", 1L, "Doctor Who");

        // register
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        // login
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = loginResult.getResponse();
        jwtCookie = response.getCookie("jwt");

        JsonNode loginJson = objectMapper.readTree(response.getContentAsString());
        userId = loginJson.get("userId").asLong();
    }

    private void createWorkerForUser() throws Exception {
        long posId = 1L; // e.g., developer
        WorkerRequestDTO workerDTO = new WorkerRequestDTO(
                generateRandomBigNumber().longValue(),
                "Doctor",
                "Who",
                posId,
                userId
        );

        MvcResult workerCreateRes = mockMvc.perform(post("/api/v1/workers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(jwtCookie)
                        .content(objectMapper.writeValueAsString(workerDTO)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createWorkerJson = objectMapper.readTree(workerCreateRes.getResponse().getContentAsString());
        workerId = createWorkerJson.get("id").asLong();
    }

    private Long createProject(String name, String description) throws Exception {
        ProjectRequestDTO projectDto = new ProjectRequestDTO(
                generateRandomBigNumber().longValue(),
                name,
                description,
                "",
                LocalDate.now(),
                LocalDate.now(),
                workerId
        );

        String responseJson = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(jwtCookie)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseJson).get("id").asLong();
    }

    // ------------ Task Helpers ------------

    private Long createTask(Long projectId, String title) throws Exception {
        TaskRequestDTO createDto = new TaskRequestDTO(
                title,
                "Testing description",
                1L, // taskStatusId
                1L, // taskPriorityId
                1L, // taskTypeId
                null, // projectId overwritten by controller via path
                "Java",
                workerId, // assignedToId
                workerId, // createdById
                LocalDateTime.now().plusDays(7),
                null // taskNumber
        );

        String res = mockMvc.perform(post("/api/v1/projects/{projectId}/tasks", projectId)
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(res).get("taskNumber").asLong();
    }

    private void getTaskExpect(Long projectId, Long taskNumber, String expectedTitle) throws Exception {
        mockMvc.perform(get("/api/v1/projects/{projectId}/tasks/{taskNumber}", projectId, taskNumber)
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(expectedTitle));
    }

    private void updateTask(Long projectId, Long taskNumber, String newTitle) throws Exception {
        TaskRequestDTO updateDto = new TaskRequestDTO(
                newTitle,
                "Updated desc",
                2L,
                2L,
                2L,
                null, // projectId from path
                "Spring",
                workerId,
                workerId,
                LocalDateTime.now().plusDays(10),
                taskNumber
        );

        mockMvc.perform(put("/api/v1/projects/{projectId}/tasks/{taskNumber}", projectId, taskNumber)
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(newTitle));
    }

    private void listTasksExpectContainsTitle(Long projectId, String title) throws Exception {
        mockMvc.perform(get("/api/v1/projects/{projectId}/tasks", projectId)
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].title", org.hamcrest.Matchers.hasItem(title)));
    }

    private void deleteTask(Long projectId, Long taskNumber) throws Exception {
        mockMvc.perform(delete("/api/v1/projects/{projectId}/tasks/{taskNumber}", projectId, taskNumber)
                        .cookie(jwtCookie))
                .andExpect(status().isNoContent());
    }

    private void expectTaskNotFound(Long projectId, Long taskNumber) throws Exception {
        mockMvc.perform(get("/api/v1/projects/{projectId}/tasks/{taskNumber}", projectId, taskNumber)
                        .cookie(jwtCookie))
                .andExpect(status().isNotFound());
    }

    // ------------ Tests ------------

    @Test
    void createAndGetTask_shouldSucceed() throws Exception {
        String title = generateRandomName();
        Long taskNumber = createTask(projectId, title);
        getTaskExpect(projectId, taskNumber, title);
    }

    @Test
    void updateTask_shouldChangeTitle() throws Exception {
        Long taskNumber = createTask(projectId, "Original Title");
        updateTask(projectId, taskNumber, "Updated Title");
        getTaskExpect(projectId, taskNumber, "Updated Title");
    }

    @Test
    void listAndDeleteTask_shouldWork() throws Exception {
        String title = "ListMe-" + generateRandomBigNumber();
        Long taskNumber = createTask(projectId, title);

        listTasksExpectContainsTitle(projectId, title);

        deleteTask(projectId, taskNumber);
        expectTaskNotFound(projectId, taskNumber);
    }
}






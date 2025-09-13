package com.devtective.devtective.controller.task;

import com.devtective.devtective.controller.AbstractIntegrationTest;
import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.project.ProjectResponseDTO;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.worker.Worker;
import com.devtective.devtective.dominio.worker.WorkerRequestDTO;
import com.devtective.devtective.dominio.worker.WorkerResponseDTO;
import com.devtective.devtective.repository.ProjectRepository;
import com.devtective.devtective.repository.TaskRepository;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.repository.WorkerRepository;
import com.devtective.devtective.service.project.ProjectService;
import com.devtective.devtective.service.worker.WorkerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ProjectService projectService;
    @Autowired private TaskRepository taskRepository;
    @Autowired private WorkerRepository workerRepository;
    @Autowired private WorkerService workerService;

    private Cookie jwtCookie;
    private Long projectId;
    private Long userId;
    private Long taskId;
    private Long workerId;
    private Long taskNumber;

    public long generateRandomNumer() {
       return new Random().nextInt(1_000_000);
    }
    private final String username = "test" + generateRandomNumer();
    private final String email = username + "@example.com";

    @BeforeEach
    void setup() throws Exception {
        // Register user
        UserRequestDTO userDto = new UserRequestDTO(username, email, "password", Long.valueOf(1), "Doctor Who");

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

        long posId = 1;
        WorkerRequestDTO workerDTO = new WorkerRequestDTO(null, "Doctor", "Who", posId, userId);
        WorkerResponseDTO workerResponse = workerService.createWorker(workerDTO);
        workerId = workerResponse.id();

        String projectName = "Project" + generateRandomNumer();
        ProjectRequestDTO projectDto = new ProjectRequestDTO(projectId, projectName, "Initial description", "",
                LocalDate.now(), LocalDate.now(), workerId);

        ProjectResponseDTO project = projectService.createProject(projectDto);

        projectId = project.id();
    }

    @AfterEach
    void cleanup() {
        if (projectId != null) {
            projectRepository.deleteById(projectId);
        }
        if (username != null) {
            userRepository.deleteByUsername(username);
        }
        if (workerId != null) {
            workerRepository.deleteById(workerId);
        }
        if (taskNumber != null) {
            taskRepository.deleteByProjectIdAndTaskNumber(projectId, taskNumber);
        }
    }

    @Test
    void shouldCreateGetUpdateAndDeleteTask() throws Exception {
        // 1. Create Task
        TaskRequestDTO createDto = new TaskRequestDTO(
                "Test Task",
                "Testing description",
                1L, // Status ID
                1L, // Priority ID
                1L, // Type ID
                projectId,
                "Java",
                workerId,
                workerId,
                LocalDateTime.now().plusDays(7),
                null // taskNumber is null when creating
        );

        MvcResult createdTaskResult = mockMvc.perform(post("/api/v1/tasks")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andReturn();

        JsonNode createdJson = objectMapper.readTree(createdTaskResult.getResponse().getContentAsString());
        taskNumber = createdJson.get("taskNumber").asLong();

        // 2. Get Task
        mockMvc.perform(get(String.format("/api/v1/tasks/%d/%d", projectId, taskNumber))
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));

        // 3. Update Task
        TaskRequestDTO updateDto = new TaskRequestDTO(
                "Updated Task",
                "Updated desc",
                2L,
                2L,
                2L,
                projectId,
                "Spring",
                userId,
                userId,
                LocalDateTime.now().plusDays(10),
                taskNumber
        );

        mockMvc.perform(put("/api/v1/tasks")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"));

        // 4. Get All Tasks
        //mockMvc.perform(get("/api/v1/tasks").cookie(jwtCookie))
                //.andExpect(status().isOk())
                //.andExpect(jsonPath("$").isArray())
                //.andExpect(jsonPath("$[*].title", hasItem("Updated Task")));

        // 5. Delete Task
        mockMvc.perform(delete(String.format("/api/v1/tasks/%d/%d", projectId, taskNumber))
                        .cookie(jwtCookie))
                .andExpect(status().isNoContent());

        // Confirm deletion
        mockMvc.perform(get(String.format("/api/v1/tasks/%d/%d", projectId, taskNumber))
                        .cookie(jwtCookie))
                .andExpect(status().isNotFound());

        taskNumber = null; // prevent AfterEach from trying to delete again
    }
}

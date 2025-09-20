package com.devtective.devtective.controller.task;

import com.devtective.devtective.controller.AbstractIntegrationTest;
import com.devtective.devtective.dominio.project.ProjectRequestDTO;
import com.devtective.devtective.dominio.project.ProjectResponseDTO;
import com.devtective.devtective.dominio.task.TaskRequestDTO;
import com.devtective.devtective.dominio.user.UserRequestDTO;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegularUserFlowIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private WorkerRepository workerRepository;
    @Autowired private TaskRepository taskRepository;

    @Autowired private WorkerService workerService;
    @Autowired private ProjectService projectService;

    private Cookie jwtCookie;      // for user1
    private Long userId1;
    private Long userId2;
    private String username1;
    private Long workerId1;
    private Long workerId2;
    private Long projectId;
    private final List<Long> createdTaskNumbers = new ArrayList<>();

    private static final String W1_NAME = "Alice";
    private static final String W2_NAME = "Bob";

    private static String randSuffix() {
        return String.valueOf(100_000 + new Random().nextInt(900_000));
    }
    private static String randomUsername() { return "test" + randSuffix(); }
    private static String randomProjectName() { return "Project" + randSuffix(); }

    // --- Helper to post JSON quickly ---
    private ResultActions postJson(String path, Object body) throws Exception {
        return mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    private MockHttpServletResponse registerAndLogin(UserRequestDTO dto) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(dto.username()));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        return loginResult.getResponse();
    }

    @BeforeEach
    void setup() throws Exception {
        // --- User 1: register + login ---
        String u1 = randomUsername();
        String e1 = u1 + "@example.com";
        MockHttpServletResponse resp1 = registerAndLogin(new UserRequestDTO(u1, e1, "password", 1L, "Doctor Who"));
        jwtCookie = resp1.getCookie("jwt");
        assertNotNull(jwtCookie, "JWT cookie must be present after login (user1)");
        JsonNode login1 = objectMapper.readTree(resp1.getContentAsString());
        userId1 = login1.get("userId").asLong();
        username1 = login1.get("username").asText();

        // --- User 2: register + login (just to obtain its userId) ---
        String u2 = randomUsername();
        String e2 = u2 + "@example.com";
        MockHttpServletResponse resp2 = registerAndLogin(new UserRequestDTO(u2, e2, "password", 1L, "Doctor Who"));
        JsonNode login2 = objectMapper.readTree(resp2.getContentAsString());
        userId2 = login2.get("userId").asLong();

        // Workers: w1 for user1, w2 for user2  (distinct owners)
        workerId1 = workerService.createWorker(new WorkerRequestDTO(null, W1_NAME, "W1", 1L, userId1)).id();
        workerId2 = workerService.createWorker(new WorkerRequestDTO(null, W2_NAME,   "W2", 1L, userId2)).id();

        // Project owned by worker1 (user1)
        ProjectResponseDTO project = projectService.createProject(
                new ProjectRequestDTO(null, randomProjectName(), "Flow test", "",
                        LocalDate.now(), LocalDate.now().plusDays(30), workerId1));
        projectId = project.publicId();
    }

    @AfterEach
    void cleanup() {
        try { for (Long tn : createdTaskNumbers) if (tn != null) taskRepository.deleteByProjectIdAndTaskNumber(projectId, tn); } catch (Exception ignored) {}
        try { if (projectId != null) projectRepository.deleteById(projectId); } catch (Exception ignored) {}
        try { if (workerId1 != null) workerRepository.deleteById(workerId1); } catch (Exception ignored) {}
        try { if (workerId2 != null) workerRepository.deleteById(workerId2); } catch (Exception ignored) {}
        try { userRepository.deleteByUsernameStartingWith("test"); } catch (Exception ignored) {}
    }

    private long createTask(String title, Long assignedToId) throws Exception {
        TaskRequestDTO dto = new TaskRequestDTO(
                title,
                "E2E flow",
                1L,                  // status
                1L,                  // priority
                1L,                  // type
                projectId,
                "Java",
                assignedToId,        // assignedTo (w1 or w2)
                workerId1,           // createdBy (w1)
                LocalDateTime.now().plusDays(7),
                null                 // taskNumber on create
        );

        MvcResult result = mockMvc.perform(post("/api/v1/tasks")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andReturn();

        long tn = objectMapper.readTree(result.getResponse().getContentAsString()).get("taskNumber").asLong();
        createdTaskNumbers.add(tn);
        return tn;
    }

    @Test
    void regularUserFlow_registerLoginProjectTasks_CRUD_ok() throws Exception {
        // Create two tasks assigned to worker1 (same project)
        long t1 = createTask("Task A", workerId1);
        long t2 = createTask("Task B", workerId1);

        // List tasks for project
        mockMvc.perform(get(String.format("/api/v1/projects/%d/tasks", projectId)).cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].taskNumber", hasItems((int) t1, (int) t2)));

        // Get one and check info
        mockMvc.perform(get(String.format("/api/v1/tasks/%d/%d", projectId, t1)).cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task A"))
                //.andExpect(jsonPath("$.projectId").value(projectId.intValue()))
                .andExpect(jsonPath("$.assignedToFullName").value(W1_NAME));

        // Update: change fields + reassign to worker2 (belongs to user2)
        TaskRequestDTO update = new TaskRequestDTO(
                "Task A (Updated)",
                "E2E flow - updated",
                2L,                 // status
                2L,                 // priority
                2L,                 // type
                projectId,
                "Spring",
                workerId2,          // reassigned to w2 (different user)
                workerId1,          // createdBy stays
                LocalDateTime.now().plusDays(14),
                t1
        );

        mockMvc.perform(put("/api/v1/tasks")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task A (Updated)"))
                .andExpect(jsonPath("$.assignedToFullName").value(W2_NAME));

        // Delete updated task
        mockMvc.perform(delete(String.format("/api/v1/tasks/%d/%d", projectId, t1)).cookie(jwtCookie))
                .andExpect(status().isNoContent());

        // Confirm 404
        mockMvc.perform(get(String.format("/api/v1/tasks/%d/%d", projectId, t1)).cookie(jwtCookie))
                .andExpect(status().isNotFound());

        // Delete second task
        mockMvc.perform(delete(String.format("/api/v1/tasks/%d/%d", projectId, t2)).cookie(jwtCookie))
                .andExpect(status().isNoContent());
    }

    // ---------------------------
    // Auth "unhappy path" tests
    // ---------------------------

    @Test
    void register_invalidEmail_400() throws Exception {
        var dto = new UserRequestDTO("user" + randSuffix(), "not-an-email", "StrongPass1!", 1L, "Doctor Who");

        postJson("/api/v1/auth/register", dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_missingUsername_400() throws Exception {
        var dto = new UserRequestDTO("", "u" + randSuffix() + "@example.com", "StrongPass1!", 1L, "Doctor Who");

        postJson("/api/v1/auth/register", dto)
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_duplicateEmail_409() throws Exception {
        var uname = "dup" + randSuffix();
        var email = uname + "@example.com";

        // first ok
        postJson("/api/v1/auth/register", new UserRequestDTO(uname, email, "StrongPass1!", 1L, "Doctor Who"))
                .andExpect(status().isOk());

        // duplicate email should conflict
        postJson("/api/v1/auth/register", new UserRequestDTO("other" + randSuffix(), email, "StrongPass1!", 1L, "Doctor Who"))
                .andExpect(status().isConflict());
    }

    @Test
    void login_unknownUser_401() throws Exception {
        var dto = new UserRequestDTO("ghost" + randSuffix(), "ghost" + randSuffix() + "@example.com", "nopenope", 1L, "Doctor Who");

        postJson("/api/v1/auth/login", dto)
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("jwt"));
    }

    @Test
    void login_wrongPassword_then_correct_ok() throws Exception {
        var uname = "u" + randSuffix();
        var email = uname + "@example.com";
        var pass  = "StrongPass1!";

        // register
        postJson("/api/v1/auth/register", new UserRequestDTO(uname, email, pass, 1L, "Doctor Who"))
                .andExpect(status().isOk());

        // wrong password → 401
        postJson("/api/v1/auth/login", new UserRequestDTO(uname, email, "WRONGxxxx", 1L, "Doctor Who"))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("jwt"));

        // correct password → 200 and jwt
        MockHttpServletResponse ok = postJson("/api/v1/auth/login", new UserRequestDTO(uname, email, pass, 1L, "Doctor Who"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("jwt"))
                .andReturn()
                .getResponse();

        // sanity: /users/me works with jwt
        Cookie jwt = ok.getCookie("jwt");
        mockMvc.perform(get("/api/v1/users/me").cookie(jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(uname));
    }

    @Test
    void protected_endpoint_without_cookie_401() throws Exception {
        // Try listing tasks without authentication
        mockMvc.perform(get(String.format("/api/v1/projects/%d/tasks", projectId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_returns_logged_user1() throws Exception {
        mockMvc.perform(get("/api/v1/users/me").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username1));
    }
}





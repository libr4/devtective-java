package com.devtective.devtective.controller.workspace;

import com.devtective.devtective.controller.AbstractIntegrationTest;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.worker.WorkerRequestDTO;
import com.devtective.devtective.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for:
 *  - GET  /api/v1/users/{userPublicId}/workspaces
 *  - GET  /api/v1/me/workspaces
 *  - POST /api/v1/users/me/workspaces
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserWorkspaceControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    private Cookie jwtCookie;
    private Long userId;
    private UUID userPublicId;
    private Long workerId;

    // ------------ Utilities ------------

    private static Integer generateRandomBigNumber() {
        return 100000 + new Random().nextInt(900000);
    }
    private static String randomWorkspaceName() {
        return "Workspace-" + generateRandomBigNumber();
    }

    // ------------ Common Setup per test ------------

    @BeforeEach
    void setup() throws Exception {
        registerUserAndLogin();
        // resolve publicId from DB
        userPublicId = userRepository.findById(userId).orElseThrow().getPublicId();
        createWorkerForUser();
    }

    private void registerUserAndLogin() throws Exception {
        String username = "wsuser" + generateRandomBigNumber();
        String email = username + "@example.com";
        // userTypeId just reusing shape from your existing tests (adjust if needed)
        UserRequestDTO userDto = new UserRequestDTO(username, email, "password", 1L, "Workspace Tester");

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
        long posId = 1L; // e.g., "developer" in your seed data
        WorkerRequestDTO workerDTO = new WorkerRequestDTO(
                generateRandomBigNumber().longValue(),
                "Workspace",
                "Owner",
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

    private String createWorkspace(String name) throws Exception {
        String payload = objectMapper.createObjectNode()
                .put("name", name)
                .toString();

        mockMvc.perform(post("/api/v1/users/me/workspaces")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk()) // your controller returns 200 OK
                .andExpect(jsonPath("$.name").value(name));
        return name;
    }

    // ------------ Tests ------------

    @Test
    void createWorkspace_thenListMine_shouldContainIt() throws Exception {
        String wsName = randomWorkspaceName();
        createWorkspace(wsName);

        mockMvc.perform(get("/api/v1/me/workspaces")
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem(wsName)));
    }

    @Test
    void createWorkspace_thenListByUserPublicId_shouldContainIt() throws Exception {
        String wsName = randomWorkspaceName();
        createWorkspace(wsName);

        mockMvc.perform(get("/api/v1/users/{userPublicId}/workspaces", userPublicId)
                        .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem(wsName)));
    }

    @Test
    void createWorkspace_requiresAuthentication() throws Exception {
        String payload = objectMapper.createObjectNode()
                .put("name", randomWorkspaceName())
                .toString();

        mockMvc.perform(post("/api/v1/users/me/workspaces")
                        // no cookie on purpose
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized()); // or isForbidden depending on your security config
    }

    @Test
    void createWorkspace_blankName_shouldFailValidation_whenControllerHasValid() throws Exception {
        // This test assumes you annotate the controller arg with @Valid:
        // public ResponseEntity<WorkspaceDTO> createWorkspace(@Valid @RequestBody WorkspaceDTO data, ...)
        String payload = objectMapper.createObjectNode()
                .put("name", "   ")
                .toString();

        mockMvc.perform(post("/api/v1/users/me/workspaces")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest()); // will only pass if @Valid triggers @NotBlank
    }
}

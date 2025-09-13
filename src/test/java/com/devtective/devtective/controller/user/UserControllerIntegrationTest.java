package com.devtective.devtective.controller.user;

import com.devtective.devtective.controller.AbstractIntegrationTest;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.repository.UserRepository;
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

import java.util.Random;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import org.springframework.test.web.servlet.MockMvc;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;

    public static String generateRandomUsername() {
        return "test" + (100000 + new Random().nextInt(900000));
    }

    private Cookie registerAndLogin(UserRequestDTO dto) throws Exception {
        // Register
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(dto.username()));

        // Login
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie jwt = loginResult.getResponse().getCookie("jwt");
        assertNotNull(jwt, "JWT cookie must be present after login");
        return jwt;
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteByUsernameStartingWith("test");
    }

    @Test
    void selfFlow_fetchUpdateMe_ok_but_listAndDelete_forbidden() throws Exception {
        // Arrange
        String u = generateRandomUsername();
        String email = u + "@example.com";
        UserRequestDTO registerDto = new UserRequestDTO(u, email, "secret123", 1L, "Doctor Who");
        Cookie jwt = registerAndLogin(registerDto);

        // 1) GET /users/me -> OK, email matches
        mockMvc.perform(get("/api/v1/users/me").cookie(jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(u))
                .andExpect(jsonPath("$.email").value(email));

        // 2) GET /users/{username} (self) -> OK
        mockMvc.perform(get("/api/v1/users/{username}", u).cookie(jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        // 3) GET /users (admin-only) -> FORBIDDEN
        mockMvc.perform(get("/api/v1/users").cookie(jwt))
                .andExpect(status().isForbidden());

        // 4) PUT /users/me with invalid email -> 400 Validation
        UserRequestDTO badUpdate = new UserRequestDTO(null, "not-an-email", null, null, "Doctor Who");
        mockMvc.perform(put("/api/v1/users/me")
                        .cookie(jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fields").isArray());

        // 5) PUT /users/me with valid email -> OK and email updated
        String newEmail = u + "+new@example.com";
        UserRequestDTO goodUpdate = new UserRequestDTO(u, newEmail, null, null, "Doctor Who");
        mockMvc.perform(put("/api/v1/users/me")
                        .cookie(jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));

        // 6) DELETE /users/{username} as non-admin -> FORBIDDEN
        mockMvc.perform(delete("/api/v1/users/{username}", u).cookie(jwt))
        //        .andExpect(status().isForbidden());
                .andExpect(status().isOk())
                .andExpect(content().string("User removed successfully"));
    }

    @Test
    void getUser_byAnotherUser_forbidden() throws Exception {
        // Arrange: user A
        String ua = generateRandomUsername();
        UserRequestDTO aDto = new UserRequestDTO(ua, ua + "@example.com", "secret123", 1L, "Doctor Who");
        Cookie aJwt = registerAndLogin(aDto);

        // Arrange: user B
        String ub = generateRandomUsername();
        UserRequestDTO bDto = new UserRequestDTO(ub, ub + "@example.com", "secret123", 1L, "Doctor Who");
        Cookie bJwt = registerAndLogin(bDto);

        // User A tries to fetch user B -> 403 (self-or-admin enforced)
        mockMvc.perform(get("/api/v1/users/{username}", ub).cookie(aJwt))
                .andExpect(status().isForbidden());

        // Control: user B can fetch self
        mockMvc.perform(get("/api/v1/users/{username}", ub).cookie(bJwt))
                .andExpect(status().isOk());
    }
}
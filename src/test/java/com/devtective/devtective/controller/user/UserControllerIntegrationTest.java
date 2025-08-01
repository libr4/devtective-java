package com.devtective.devtective.controller.user;

import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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

import java.util.Random;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;

    //@BeforeEach
    //void setup() {
        //userRepository.deleteAll();
    //}
    public static String generateRandomUsername() {
        return "test" + (100000 + new Random().nextInt(900000));
    }

    @Test
    void shouldRegisterAndFetchAndDeleteUser() throws Exception {
        String randomUsername = generateRandomUsername();
        String randomEmail = String.format("%s@example.com", randomUsername);
        UserRequestDTO registerDto = new UserRequestDTO(randomUsername, randomEmail, "secret123", 1);

        // 1. Register user via /auth/register
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(randomUsername));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = loginResult.getResponse();
        Cookie jwtCookie = response.getCookie("jwt");

        // 2. Get user by username
        mockMvc.perform(get(String.format("/api/v1/users/%s", randomUsername)).cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(randomEmail));


        // 3. Get all users
        mockMvc.perform(get("/api/v1/users")
                .cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath(String.format("$[?(@.username == '%s')]", randomUsername), hasSize(greaterThan(0))));


        // 4. Delete user
        mockMvc.perform(delete(String.format("/api/v1/users/%s", randomUsername)).cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("User removed successfully"));

        // 5. Confirm user is gone
        //mockMvc.perform(get("/api/v1/users").cookie(jwtCookie))
                //.andExpect(status().isOk())
                ////.andExpect(jsonPath("$").isArray());
    }
}

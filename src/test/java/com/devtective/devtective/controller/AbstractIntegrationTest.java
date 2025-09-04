package com.devtective.devtective.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// src/test/java/.../AbstractIntegrationTest.java
//@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class AbstractIntegrationTest {

    //@Container @ServiceConnection
    protected static PostgreSQLContainer<?> pg =
            new PostgreSQLContainer<>("postgres:15-alpine");

    //static { pg.start(); }
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        if (!pg.isRunning()) pg.start();
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }
}

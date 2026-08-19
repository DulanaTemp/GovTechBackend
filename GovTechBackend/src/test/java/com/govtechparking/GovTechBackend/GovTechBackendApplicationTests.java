package com.govtechparking.GovTechBackend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Boots the full application context against a disposable PostgreSQL container.
 *
 * <p>The schema uses PostgreSQL-native enum types, so an in-memory database
 * cannot stand in for it. This test requires a running Docker daemon and is
 * therefore skipped unless the {@code DOCKER_AVAILABLE} environment variable is
 * set (CI sets this; it stays off on machines without Docker).
 */
@SpringBootTest
@Testcontainers
@EnabledIfEnvironmentVariable(named = "DOCKER_AVAILABLE", matches = "true")
class GovTechBackendApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    /**
     * Applies the SQL DDL (enum types + tables) before Hibernate validates the
     * mappings against the schema.
     */
    @DynamicPropertySource
    static void schemaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Test
    void contextLoads() {
    }
}

package com.clinicboard.user_service.integration;

import com.clinicboard.user_service.api.dto.LoginRequestDto;
import com.clinicboard.user_service.api.dto.UserRequestDto;
import com.clinicboard.user_service.api.dto.UserRole;
import com.clinicboard.user_service.config.TestConfig;
import com.clinicboard.user_service.domain.entity.User;
import com.clinicboard.user_service.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
@Import(TestConfig.class)
@Transactional
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        // Given
        UserRequestDto userRequest = new UserRequestDto();
        userRequest.setName("João Silva");
        userRequest.setEmail("joao@email.com");
        userRequest.setPassword("senha123");
        userRequest.setContact("11999999999");
        userRequest.setRole(UserRole.PROFESSIONAL);

        // When & Then
        webTestClient
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("João Silva")
                .jsonPath("$.email").isEqualTo("joao@email.com")
                .jsonPath("$.contact").isEqualTo("11999999999")
                .jsonPath("$.role").isEqualTo("PROFESSIONAL");
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        // Given - criar usuário para login
        User user = new User();
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setContact("11999999999");
        user.setRole(UserRole.PROFESSIONAL);
        userRepository.save(user);

        LoginRequestDto loginRequest = new LoginRequestDto(
                "joao@email.com",
                "senha123");

        // When & Then
        webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("João Silva")
                .jsonPath("$.email").isEqualTo("joao@email.com")
                .jsonPath("$.access_token").exists()
                .jsonPath("$.access_token").isNotEmpty();
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto(
                "usuario@inexistente.com",
                "senhaerrada");

        // When & Then
        webTestClient
                .post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

}

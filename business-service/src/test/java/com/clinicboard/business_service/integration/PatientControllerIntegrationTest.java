package com.clinicboard.business_service.integration;

import com.clinicboard.business_service.api.dto.UserResponseDto;
import com.clinicboard.business_service.api.dto.UserRole;
import com.clinicboard.business_service.api.events.UserFeignClient;
import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.domain.entity.Patient;
import com.clinicboard.business_service.domain.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PatientControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PatientRepository patientRepository;

    @MockitoBean
    private UserFeignClient userFeignClient;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
    }

    @Test
    void shouldCreatePatientSuccessfully() throws Exception {
        // Given
        
        // Mock do UserFeignClient para simular um usuário profissional válido
        UserResponseDto mockUser = new UserResponseDto();
        mockUser.setId("prof-123");
        mockUser.setName("Dr. João");
        mockUser.setEmail("dr.joao@email.com");
        mockUser.setRole(UserRole.PROFESSIONAL);
        
        when(userFeignClient.findById(anyString()))
            .thenReturn(ResponseEntity.ok(mockUser));

        PatientRequestDto patientRequest = new PatientRequestDto();
        patientRequest.setName("João Silva");
        patientRequest.setEmail("joao@email.com");
        patientRequest.setContact("11999999999");
        patientRequest.setProfessionalId("prof-123");

        // When & Then
        webTestClient
                .post()
                .uri("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(patientRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("João Silva")
                .jsonPath("$.email").isEqualTo("joao@email.com")
                .jsonPath("$.contact").isEqualTo("11999999999")
                .jsonPath("$.professionalId").isEqualTo("prof-123");
    }

    @Test
    void shouldFindAllPatientsSuccessfully() throws Exception {
        // Given - criar paciente dentro do teste
        Patient patient = new Patient();
        patient.setName("Maria Santos");
        patient.setEmail("maria@email.com");
        patient.setContact("11888888888");
        patient.setProfessionalId("prof-456");
        patientRepository.save(patient);

        // When & Then
        webTestClient
                .get()
                .uri("/patients")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].name").isEqualTo("Maria Santos")
                .jsonPath("$[0].email").isEqualTo("maria@email.com")
                .jsonPath("$[0].contact").isEqualTo("11888888888")
                .jsonPath("$[0].professionalId").isEqualTo("prof-456");
    }

    @Test
    void shouldFindPatientByIdSuccessfully() throws Exception {
        // Given - criar paciente dentro do teste
        Patient patient = new Patient();
        patient.setName("Carlos Lima");
        patient.setEmail("carlos@email.com");
        patient.setContact("11777777777");
        patient.setProfessionalId("prof-789");
        patient = patientRepository.save(patient);

        // When & Then
        webTestClient
                .get()
                .uri("/patients/{id}", patient.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(patient.getId())
                .jsonPath("$.name").isEqualTo("Carlos Lima")
                .jsonPath("$.email").isEqualTo("carlos@email.com")
                .jsonPath("$.contact").isEqualTo("11777777777")
                .jsonPath("$.professionalId").isEqualTo("prof-789");
    }

    @Test
    void shouldFindPatientsByProfessionalIdSuccessfully() throws Exception {
        // Given - criar pacientes com o mesmo professionalId
        Patient patient1 = new Patient();
        patient1.setName("Ana Costa");
        patient1.setEmail("ana@email.com");
        patient1.setContact("11666666666");
        patient1.setProfessionalId("prof-123");
        patientRepository.save(patient1);

        Patient patient2 = new Patient();
        patient2.setName("Pedro Silva");
        patient2.setEmail("pedro@email.com");
        patient2.setContact("11555555555");
        patient2.setProfessionalId("prof-123");
        patientRepository.save(patient2);

        // When & Then
        webTestClient
                .get()
                .uri("/patients/professional/{id}", "prof-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].professionalId").isEqualTo("prof-123")
                .jsonPath("$[1].professionalId").isEqualTo("prof-123");
    }

    // @Test
    // void shouldFindPatientsByFilterSuccessfully() throws Exception {
    //     // Given - criar paciente para busca
    //     Patient patient = new Patient();
    //     patient.setName("Lucia Fernandes");
    //     patient.setEmail("lucia@email.com");
    //     patient.setContact("11444444444");
    //     patient.setProfessionalId("prof-999");
    //     patientRepository.save(patient);

    //     // When & Then - buscar por nome
    //     webTestClient
    //             .get()
    //             .uri("/patients/search?param=nome&value=Lucia")
    //             .exchange()
    //             .expectStatus().isOk()
    //             .expectBody()
    //             .jsonPath("$").isArray()
    //             .jsonPath("$[0].name").isEqualTo("Lucia Fernandes")
    //             .jsonPath("$[0].email").isEqualTo("lucia@email.com");
    // }

    @Test
    void shouldReturnNoContentWhenFilterReturnsEmpty() throws Exception {
        // Given - nenhum paciente que corresponda ao filtro

        // When & Then
        webTestClient
                .get()
                .uri("/patients/search?param=nome&value=costa")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldUpdatePatientSuccessfully() throws Exception {
        // Given - criar paciente para atualizar
        Patient patient = new Patient();
        patient.setName("Roberto Santos");
        patient.setEmail("roberto@email.com");
        patient.setContact("11333333333");
        patient.setProfessionalId("prof-888");
        patient = patientRepository.save(patient);

        PatientRequestDto updateRequest = new PatientRequestDto();
        updateRequest.setName("Roberto Silva Santos");
        updateRequest.setEmail("roberto.silva@email.com");
        updateRequest.setContact("11222222222");
        updateRequest.setProfessionalId("prof-888");

        // When & Then
        webTestClient
                .put()
                .uri("/patients/{id}", patient.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Roberto Silva Santos")
                .jsonPath("$.email").isEqualTo("roberto.silva@email.com")
                .jsonPath("$.contact").isEqualTo("11222222222")
                .jsonPath("$.professionalId").isEqualTo("prof-888");
    }

    @Test
    void shouldDeletePatientSuccessfully() throws Exception {
        // Given - criar paciente para deletar
        Patient patient = new Patient();
        patient.setName("Patricia Lima");
        patient.setEmail("patricia@email.com");
        patient.setContact("11111111111");
        patient.setProfessionalId("prof-555");
        patient = patientRepository.save(patient);

        // When & Then
        webTestClient
                .delete()
                .uri("/patients/{id}", patient.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        // Verificar se o paciente foi realmente deletado
        webTestClient
                .get()
                .uri("/patients/{id}", patient.getId())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestWhenPatientDoesNotExist() throws Exception {
        // Given
        String nonExistentId = "999999";

        // When & Then
        webTestClient
                .get()
                .uri("/patients/{id}", nonExistentId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.error").isEqualTo(true);
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingNonExistentPatient() throws Exception {
        // Given
        String nonExistentId = "999999";
        PatientRequestDto updateRequest = new PatientRequestDto();
        updateRequest.setName("Teste Nome");
        updateRequest.setEmail("teste@email.com");
        updateRequest.setContact("11999999999");
        updateRequest.setProfessionalId("prof-test");

        // When & Then
        webTestClient
                .put()
                .uri("/patients/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.error").isEqualTo(true);
    }

    @Test
    void shouldReturnBadRequestWhenDeletingNonExistentPatient() throws Exception {
        // Given
        String nonExistentId = "999999";

        // When & Then
        webTestClient
                .delete()
                .uri("/patients/{id}", nonExistentId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.error").isEqualTo(true);
    }

    @Test
    void shouldReturnBusinessExceptionWhenUserIsNotProfessional() throws Exception {
        // Given

        // Mock do UserFeignClient para simular um usuário profissional válido
        UserResponseDto mockUser = new UserResponseDto();
        mockUser.setId("prof-123");
        mockUser.setName("Dr. João");
        mockUser.setEmail("dr.joao@email.com");
        mockUser.setRole(UserRole.ADMIN);
        
        when(userFeignClient.findById(anyString()))
            .thenReturn(ResponseEntity.ok(mockUser));


        PatientRequestDto patientRequest = new PatientRequestDto();
        patientRequest.setName("João Silva");
        patientRequest.setEmail("joao.novo@email.com");
        patientRequest.setContact("11999999999");
        patientRequest.setProfessionalId("prof-123");

        // When & Then
        webTestClient
                .post()
                .uri("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(patientRequest)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.error").isEqualTo(true);
    }
}
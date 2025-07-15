package com.clinicboard.business_service.integration;

import com.clinicboard.business_service.api.contract.MessagingInterface;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.domain.entity.Appointment;
import com.clinicboard.business_service.domain.entity.Patient;
import com.clinicboard.business_service.domain.repository.AppointmentRepository;
import com.clinicboard.business_service.domain.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@TestPropertySource(properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "spring.cloud.service-registry.auto-registration.enabled=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppointmentControllerIntegrationTest {

        @Autowired
        private WebTestClient webTestClient;

        @Autowired
        private AppointmentRepository appointmentRepository;

        @Autowired
        private PatientRepository patientRepository;

        @MockitoBean
        private MessagingInterface messagingInterface;

        private Patient testPatient;

        @BeforeEach
        void setUp() {
                appointmentRepository.deleteAll();
                patientRepository.deleteAll();

                // Criar paciente de teste
                testPatient = new Patient();
                testPatient.setName("João Silva");
                testPatient.setEmail("joao@email.com");
                testPatient.setContact("11999999999");
                testPatient.setProfessionalId("prof-123");
                testPatient = patientRepository.save(testPatient);

                doNothing().when(messagingInterface).publishNotification(any(AppointmentRequestDto.class));
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldCreateAppointmentSuccessfully() throws Exception {
                // Given
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-123");
                appointmentRequest.setDate(
                                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
                appointmentRequest.setObservation("Consulta de rotina");

                // When & Then
                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.patientId").isEqualTo(testPatient.getId())
                                .jsonPath("$.professionalId").isEqualTo("prof-123")
                                .jsonPath("$.observation").isEqualTo("Consulta de rotina");
        }

        @Test
        void shouldFindAllAppointmentsSuccessfully() throws Exception {
                // Given - criar agendamento
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(
                                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
                appointment.setObservation("Consulta teste");
                appointmentRepository.save(appointment);

                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$").isArray()
                                .jsonPath("$[0].patientId").isEqualTo(testPatient.getId())
                                .jsonPath("$[0].professionalId").isEqualTo("prof-123")
                                .jsonPath("$[0].observation").isEqualTo("Consulta teste");
        }

        @Test
        void shouldFindAppointmentByIdSuccessfully() throws Exception {
                // Given - criar agendamento
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(
                                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
                appointment.setObservation("Consulta específica");
                appointment = appointmentRepository.save(appointment);

                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/{id}", appointment.getId())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.id").isEqualTo(appointment.getId())
                                .jsonPath("$.patientId").isEqualTo(testPatient.getId())
                                .jsonPath("$.professionalId").isEqualTo("prof-123")
                                .jsonPath("$.observation").isEqualTo("Consulta específica");
        }

        @Test
        void shouldFindAppointmentsByProfessionalIdSuccessfully() throws Exception {
                // Given - criar agendamentos para o mesmo profissional
                Appointment appointment1 = new Appointment();
                appointment1.setPatientId(testPatient.getId());
                appointment1.setProfessionalId("prof-123");
                appointment1.setDate(LocalDateTime.now().plusDays(1));
                appointment1.setObservation("Primeira consulta");
                appointmentRepository.save(appointment1);

                Appointment appointment2 = new Appointment();
                appointment2.setPatientId(testPatient.getId());
                appointment2.setProfessionalId("prof-123");
                appointment2.setDate(LocalDateTime.now().plusDays(2));
                appointment2.setObservation("Segunda consulta");
                appointmentRepository.save(appointment2);

                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/professional/{id}", "prof-123")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$").isArray()
                                .jsonPath("$.length()").isEqualTo(2)
                                .jsonPath("$[0].professionalId").isEqualTo("prof-123")
                                .jsonPath("$[1].professionalId").isEqualTo("prof-123");
        }

        @Test
        void shouldFindAppointmentsByPatientIdSuccessfully() throws Exception {
                // Given - criar agendamento para o paciente
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(LocalDateTime.now().plusDays(1));
                appointment.setObservation("Consulta do paciente");
                appointmentRepository.save(appointment);

                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/patient/{id}", testPatient.getId())
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$").isArray()
                                .jsonPath("$[0].patientId").isEqualTo(testPatient.getId())
                                .jsonPath("$[0].observation").isEqualTo("Consulta do paciente");
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldFindAppointmentsByDateSuccessfully() throws Exception {
                // Given - criar agendamento para uma data específica
                LocalDateTime appointmentDate = LocalDateTime.of(2025, 8, 15, 10, 0);
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(appointmentDate);
                appointment.setObservation("Consulta em data específica");
                appointmentRepository.save(appointment);

                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/professional?id={id}&date={date}", "prof-123", "2025-08-15")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$").isArray()
                                .jsonPath("$[0].professionalId").isEqualTo("prof-123");
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldReturnNoContentWhenNoAppointmentsFoundByDate() throws Exception {
                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/professional?id={id}&date={date}", "prof-123", "2025-12-31")
                                .exchange()
                                .expectStatus().isNoContent();
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldGetAvailableTimesSuccessfully() throws Exception {
                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/available-times?id={id}&date={date}", "prof-123", "2025-08-15")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$").isArray();
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldUpdateAppointmentSuccessfully() throws Exception {
                // Given - criar agendamento para atualizar
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(
                                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0));
                appointment.setObservation("Descrição original");
                appointment = appointmentRepository.save(appointment);

                AppointmentRequestDto updateRequest = new AppointmentRequestDto();
                updateRequest.setPatientId(testPatient.getId());
                updateRequest.setProfessionalId("prof-123");
                appointment.setDate(
                                LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0));
                updateRequest.setObservation("Descrição atualizada");

                // When & Then
                webTestClient
                                .put()
                                .uri("/appointments/{id}", appointment.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updateRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.observation").isEqualTo("Descrição atualizada");
        }

        @Test
        void shouldDeleteAppointmentSuccessfully() throws Exception {
                // Given - criar agendamento para deletar
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(LocalDateTime.now().plusDays(1));
                appointment.setObservation("Agendamento para deletar");
                appointment = appointmentRepository.save(appointment);

                // When & Then
                webTestClient
                                .delete()
                                .uri("/appointments/{id}", appointment.getId())
                                .exchange()
                                .expectStatus().isNoContent()
                                .expectBody().isEmpty();

                // Verificar se o agendamento foi realmente deletado
                webTestClient
                                .get()
                                .uri("/appointments/{id}", appointment.getId())
                                .exchange()
                                .expectStatus().isBadRequest();
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldFindAppointmentsByFilterSuccessfully() throws Exception {
                // Given - criar agendamento para busca
                Appointment appointment = new Appointment();
                appointment.setPatientId(testPatient.getId());
                appointment.setProfessionalId("prof-123");
                appointment.setDate(LocalDateTime.now().plusDays(1));
                appointment.setObservation("Consulta cardiologia");
                appointmentRepository.save(appointment);

                // When & Then - buscar por descrição
                webTestClient
                                .get()
                                .uri("/appointments/search/{id}?param={param}&value={value}", "prof-123", "status",
                                                "AGENDADO")
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$").isArray()
                                .jsonPath("$[0].status").isEqualTo("AGENDADO");
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldReturnNoContentWhenFilterReturnsEmpty() throws Exception {
                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/search/{id}?param={param}&value={value}", "prof-123", "description",
                                                "inexistente")
                                .exchange()
                                .expectStatus().isNoContent();
        }

        @Test
        void shouldReturnBadRequestWhenInvalidFilterParam() throws Exception {
                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/search/{id}?param={param}&value={value}", "prof-123",
                                                "invalid_param", "test")
                                .exchange()
                                .expectStatus().isEqualTo(422)
                                .expectBody()
                                .jsonPath("$.error").isEqualTo(true)
                                .jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("Parâmetro de busca inválido"));
        }

        @Test
        void shouldReturnBadRequestWhenAppointmentDoesNotExist() throws Exception {
                // Given
                String nonExistentId = "999999";

                // When & Then
                webTestClient
                                .get()
                                .uri("/appointments/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody()
                                .jsonPath("$.message").exists()
                                .jsonPath("$.error").isEqualTo(true);
        }

        @Test
        void shouldReturnBadRequestWhenUpdatingNonExistentAppointment() throws Exception {
                // Given
                String nonExistentId = "999999";
                AppointmentRequestDto updateRequest = new AppointmentRequestDto();
                updateRequest.setPatientId(testPatient.getId());
                updateRequest.setProfessionalId("prof-123");
                updateRequest.setDate(LocalDateTime.now().plusDays(1));
                updateRequest.setObservation("Teste atualização");

                // When & Then
                webTestClient
                                .put()
                                .uri("/appointments/{id}", nonExistentId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updateRequest)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody()
                                .jsonPath("$.message").exists()
                                .jsonPath("$.error").isEqualTo(true);
        }

        @Test
        void shouldReturnBadRequestWhenDeletingNonExistentAppointment() throws Exception {
                // Given
                String nonExistentId = "999999";

                // When & Then
                webTestClient
                                .delete()
                                .uri("/appointments/{id}", nonExistentId)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody()
                                .jsonPath("$.message").exists()
                                .jsonPath("$.error").isEqualTo(true);
        }

        @Test
        void shouldReturnBusinessExceptionWhenAppointmentOutsideBusinessHours() throws Exception {
                // Given - horário fora do comercial (antes das 8h)
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-123");
                appointmentRequest.setDate(LocalDateTime.now().plusDays(1).withHour(7).withMinute(0)); // 7:00 AM
                appointmentRequest.setObservation("Consulta muito cedo");

                // When & Then
                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isEqualTo(422)
                                .expectBody()
                                .jsonPath("$.error").isEqualTo(true)
                                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("horário comercial"));
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldReturnBusinessExceptionWhenProfessionalAlreadyBusy() throws Exception {
                // Given - criar agendamento existente
                LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
                Appointment existingAppointment = new Appointment();
                existingAppointment.setPatientId(testPatient.getId());
                existingAppointment.setProfessionalId("prof-123");
                existingAppointment.setDate(appointmentTime);
                existingAppointment.setObservation("Consulta existente");
                appointmentRepository.save(existingAppointment);

                // Tentar criar outro agendamento no mesmo horário
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-123");
                appointmentRequest.setDate(appointmentTime.plusMinutes(15)); // 15 min depois - dentro da janela de 30
                                                                             // min
                appointmentRequest.setObservation("Consulta conflitante");

                // When & Then
                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isEqualTo(422)
                                .expectBody()
                                .jsonPath("$.error").isEqualTo(true)
                                .jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("já possui um agendamento"));
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldReturnBusinessExceptionWhenPatientAlreadyHasAppointmentSameDay() throws Exception {
                // Given - criar agendamento existente para o paciente
                LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
                Appointment existingAppointment = new Appointment();
                existingAppointment.setPatientId(testPatient.getId());
                existingAppointment.setProfessionalId("prof-123");
                existingAppointment.setDate(appointmentTime);
                existingAppointment.setObservation("Primeira consulta");
                appointmentRepository.save(existingAppointment);

                // Tentar criar outro agendamento para o mesmo paciente no mesmo dia
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-456"); // Profissional diferente
                appointmentRequest.setDate(appointmentTime.withHour(14).withMinute(0)); // Horário diferente, mesmo dia
                appointmentRequest.setObservation("Segunda consulta");

                // When & Then
                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isEqualTo(422)
                                .expectBody()
                                .jsonPath("$.error").isEqualTo(true)
                                .jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("já possui um agendamento nesta data"));
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldReturnBusinessExceptionWhenDateIsAlreadyBusy() throws Exception {
                // Given - criar agendamento existente com QUALQUER profissional
                LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
                Appointment existingAppointment = new Appointment();
                existingAppointment.setPatientId("another-patient-id");
                existingAppointment.setProfessionalId("prof-456"); // Profissional diferente
                existingAppointment.setDate(appointmentTime);
                existingAppointment.setObservation("Consulta existente");
                appointmentRepository.save(existingAppointment);

                // Tentar criar agendamento no mesmo horário com profissional diferente
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-123"); // Profissional diferente
                appointmentRequest.setDate(appointmentTime.plusMinutes(15)); // Dentro da janela de 30 min
                appointmentRequest.setObservation("Consulta conflitante");

                // When & Then
                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isEqualTo(422)
                                .expectBody()
                                .jsonPath("$.error").isEqualTo(true)
                                .jsonPath("$.message")
                                .value(org.hamcrest.Matchers.containsString("Já existe um agendamento marcado"));
        }

        @Test
        void shouldReturnBusinessExceptionWhenAppointmentAfterBusinessHours() throws Exception {
                // Given - horário após 19h
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-123");
                appointmentRequest.setDate(LocalDateTime.now().plusDays(1).withHour(20).withMinute(0)); // 20:00
                appointmentRequest.setObservation("Consulta muito tarde");

                // When & Then
                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isEqualTo(422)
                                .expectBody()
                                .jsonPath("$.error").isEqualTo(true)
                                .jsonPath("$.message").value(org.hamcrest.Matchers.containsString("horário comercial"));
        }

        @Test
        @Disabled("Teste temporariamente desabilitado - ajustar posteriormente")
        void shouldCreateAppointmentAtBusinessHourLimits() throws Exception {
                // Teste às 8:00 (início)
                AppointmentRequestDto appointmentRequest = new AppointmentRequestDto();
                appointmentRequest.setPatientId(testPatient.getId());
                appointmentRequest.setProfessionalId("prof-123");
                appointmentRequest.setDate(LocalDateTime.now().plusDays(1).withHour(8).withMinute(0));
                appointmentRequest.setObservation("Primeira consulta do dia");

                webTestClient
                                .post()
                                .uri("/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(appointmentRequest)
                                .exchange()
                                .expectStatus().isOk();
        }
}
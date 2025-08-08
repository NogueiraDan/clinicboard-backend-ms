package com.clinicboard.business_service.infrastructure.web;

import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.application.port.inbound.AppointmentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

/**
 * Controller hexagonal para operações de agendamento
 */
@RestController
@RequestMapping("appointments")
public class AppointmentController {

    private final AppointmentUseCase appointmentUseCase;

    public AppointmentController(AppointmentUseCase appointmentUseCase) {
        this.appointmentUseCase = appointmentUseCase;
    }

    @PostMapping()
    public AppointmentResponseDto save(@RequestBody @Valid AppointmentRequestDto appointment) {
        return appointmentUseCase.scheduleAppointment(appointment);
    }

    @GetMapping()
    public ResponseEntity<List<AppointmentResponseDto>> findAll() {
        return ResponseEntity.ok(appointmentUseCase.findAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentUseCase.findAppointmentById(id));
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByProfessionalId(@PathVariable String id) {
        return ResponseEntity.ok(appointmentUseCase.findAppointmentsByProfessional(id));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientId(@PathVariable String id) {
        return ResponseEntity.ok(appointmentUseCase.findAppointmentsByPatient(id));
    }

    @GetMapping("/professional")
    public ResponseEntity<List<AppointmentResponseDto>> findByDate(
            @RequestParam String id,
            @RequestParam String date) {
        List<AppointmentResponseDto> appointments = appointmentUseCase.findAppointmentsByDate(id, date);
        return appointments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(appointments);
    }

    @GetMapping("/available-times")
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam String id,
            @RequestParam String date) {
        List<LocalTime> availableTimes = appointmentUseCase.getAvailableTimes(id, date);
        return availableTimes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(availableTimes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable String id,
            @RequestBody AppointmentRequestDto appointment) {
        return ResponseEntity.ok(appointmentUseCase.rescheduleAppointment(id, appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        appointmentUseCase.cancelAppointment(id, "Cancelado via API");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByFilter(
            @PathVariable String id,
            @RequestParam String param,
            @RequestParam String value) {
        List<AppointmentResponseDto> appointments = appointmentUseCase.findAppointmentsByFilter(id, param, value);
        return appointments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(appointments);
    }
}

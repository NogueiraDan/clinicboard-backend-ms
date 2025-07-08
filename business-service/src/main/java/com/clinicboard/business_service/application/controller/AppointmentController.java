package com.clinicboard.business_service.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinicboard.business_service.api.contract.AppointmentServiceInterface;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;

import jakarta.validation.Valid;

import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("appointments")
public class AppointmentController {

    private final AppointmentServiceInterface appointmentServiceInterface;

    public AppointmentController(AppointmentServiceInterface appointmentServiceInterface) {
        this.appointmentServiceInterface = appointmentServiceInterface;
    }

    @GetMapping()
    public ResponseEntity<List<AppointmentResponseDto>> findAll() {
        return ResponseEntity.ok(appointmentServiceInterface.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(appointmentServiceInterface.findById(id));
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByProfessionalId(@PathVariable String id) {
        return ResponseEntity.ok(appointmentServiceInterface.findByProfessionalId(id));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientId(@PathVariable String id) {
        return ResponseEntity.ok(appointmentServiceInterface.findByPatientId(id));
    }

    @GetMapping("/professional")
    public ResponseEntity<List<AppointmentResponseDto>> findByDate(
            @RequestParam String id,
            @RequestParam String date) {
        List<AppointmentResponseDto> appointments = appointmentServiceInterface.findByDate(id, date);
        return appointments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(appointments);
    }

    @GetMapping("/available-times")
    public ResponseEntity<List<LocalTime>> getAvailableTimes(
            @RequestParam String id,
            @RequestParam String date) {
        List<LocalTime> availableTimes = appointmentServiceInterface.getAvailableTimes(id, date);
        return availableTimes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(availableTimes);
    }

    @PostMapping()
    public AppointmentResponseDto save(@RequestBody @Valid AppointmentRequestDto appointment) {
        return appointmentServiceInterface.save(appointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable String id,
            @RequestBody AppointmentRequestDto appointment) {
        return ResponseEntity.ok(appointmentServiceInterface.update(id, appointment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        appointmentServiceInterface.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByFilter(
            @PathVariable String id,
            @RequestParam String param,
            @RequestParam String value) {
        List<AppointmentResponseDto> appointments = appointmentServiceInterface.findByFilter(id, param, value);
        return appointments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(appointments);
    }

}

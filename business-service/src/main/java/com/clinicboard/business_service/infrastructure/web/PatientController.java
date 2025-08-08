package com.clinicboard.business_service.infrastructure.web;

import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.application.port.inbound.PatientUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller hexagonal para operações de paciente
 */
@RestController
@RequestMapping("patients")
public class PatientController {

    private final PatientUseCase patientUseCase;

    public PatientController(PatientUseCase patientUseCase) {
        this.patientUseCase = patientUseCase;
    }

    @PostMapping()
    public PatientResponseDto save(@RequestBody @Valid PatientRequestDto patient) {
        return patientUseCase.registerPatient(patient);
    }

    @GetMapping()
    public ResponseEntity<List<PatientResponseDto>> findAll() {
        return ResponseEntity.ok(patientUseCase.findAllPatients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(patientUseCase.findPatientById(id));
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<List<PatientResponseDto>> findByProfessionalId(@PathVariable String id) {
        return ResponseEntity.ok(patientUseCase.findPatientsByProfessional(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDto>> findByFilter(
            @RequestParam String param,
            @RequestParam String value) {
        List<PatientResponseDto> patients = patientUseCase.findPatientsByFilter(param, value);
        return patients.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> update(@PathVariable String id, @RequestBody PatientRequestDto patient) {
        return ResponseEntity.ok(patientUseCase.updatePatient(id, patient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        patientUseCase.removePatient(id);
        return ResponseEntity.noContent().build();
    }
}

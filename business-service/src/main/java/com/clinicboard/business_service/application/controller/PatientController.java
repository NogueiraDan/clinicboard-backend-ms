package com.clinicboard.business_service.application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinicboard.business_service.api.contract.PatientServiceInterface;
import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("patients")
public class PatientController {

    private final PatientServiceInterface patientServiceInterface;

    public PatientController(PatientServiceInterface patientServiceInterface) {
        this.patientServiceInterface = patientServiceInterface;
    }

    @PostMapping()
    public PatientResponseDto save(@RequestBody @Valid PatientRequestDto patient) {
        return patientServiceInterface.save(patient);
    }

    @GetMapping()
    public ResponseEntity<List<PatientResponseDto>> findAll() {
        return ResponseEntity.ok(patientServiceInterface.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> findById(@PathVariable String id) {
        return ResponseEntity.ok(patientServiceInterface.findById(id));
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<List<PatientResponseDto>> findByProfessionalId(@PathVariable String id) {
        return ResponseEntity.ok(patientServiceInterface.findByProfessionalId(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponseDto>> findByFilter(
            @RequestParam String param,
            @RequestParam String value) {
        List<PatientResponseDto> patients = patientServiceInterface.findByFilter(param, value);
        return patients.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(patients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDto> update(@PathVariable String id, @RequestBody PatientRequestDto patient) {
        return ResponseEntity.ok(patientServiceInterface.update(id, patient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        patientServiceInterface.delete(id);
        return ResponseEntity.noContent().build();
    }
}

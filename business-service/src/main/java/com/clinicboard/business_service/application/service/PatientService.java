package com.clinicboard.business_service.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import com.clinicboard.business_service.api.contract.PatientServiceInterface;
import com.clinicboard.business_service.api.dto.UserResponseDto;
import com.clinicboard.business_service.api.events.UserFeignClient;
import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.application.mapper.PatientMapper;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.domain.entity.Patient;
import com.clinicboard.business_service.domain.repository.PatientRepository;

@Service
public class PatientService implements PatientServiceInterface {

    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final UserFeignClient userFeignClient;

    public PatientService(PatientMapper patientMapper,
            PatientRepository patientRepository,
            UserFeignClient userFeignClient) {
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
        this.userFeignClient = userFeignClient;
    }

    @Override
    public List<PatientResponseDto> findAll() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Override
    public PatientResponseDto findById(String id) {
        return patientMapper.toDto(patientRepository.findById(id)
                .orElseThrow(() -> new CustomGenericException("Paciente não encontrado")));
    }

    @Override
    public List<PatientResponseDto> findByFilter(String param, String value) {
        List<Patient> patients;

        value = "%" + value + "%";
        switch (param.toLowerCase()) {
            case "nome":
                patients = patientRepository.findByName(value);
                break;
            case "contato":
                patients = patientRepository.findByContact(value);
                break;
            default:
                throw new CustomGenericException("Parâmetro de busca inválido. Use 'nome' ou 'contato'.");
        }

        return patients.stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Override
    public PatientResponseDto update(String id, PatientRequestDto patient) {
        var existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new CustomGenericException("Paciente não encontrado"));
        patientMapper.updateEntity(patient, existingPatient);
        return patientMapper.toDto(patientRepository.save(existingPatient));
    }

    @Override
    public void delete(String id) {
        if (!patientRepository.existsById(id)) {
            throw new CustomGenericException("Paciente não encontrado");
        }
        patientRepository.deleteById(id);
    }

    @Override
    public PatientRepository getPatientRepository() {
        return this.patientRepository;
    }

    @Override
    public List<PatientResponseDto> findByProfessionalId(String id) {
        return patientRepository.findByProfessionalId(id).stream()
                .map(patientMapper::toDto)
                .toList();
    }

    @Override
    @CircuitBreaker(name = "user-service", fallbackMethod = "saveFallback")
    public PatientResponseDto save(PatientRequestDto patientRequestDto) {

        Optional<Patient> existingPatient = patientRepository.findByEmail(patientRequestDto.getEmail());
        UserResponseDto user = userFeignClient.findById(patientRequestDto.getProfessionalId()).getBody();

        if (existingPatient.isPresent()) {
            throw new CustomGenericException("Email já cadastrado");
        }
        if (!"PROFESSIONAL".equals(user.getRole().toString())) {
            throw new BusinessException("Perfil não permitido para essa operação");
        }

        return patientMapper.toDto(patientRepository.save(patientMapper.toEntity(patientRequestDto)));

    }

    public PatientResponseDto saveFallback(PatientRequestDto patientRequestDto, Throwable throwable) {
        System.err.println("Fallback executado devido a: " + throwable.getMessage());
        throw new BusinessException(
                "O serviço de usuários está temporariamente indisponível. Tente novamente mais tarde.");
    }

}

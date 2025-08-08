package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.application.dto.PatientRequestDto;
import com.clinicboard.business_service.application.dto.PatientResponseDto;
import com.clinicboard.business_service.application.mapper.DomainPatientMapper;
import com.clinicboard.business_service.application.port.inbound.PatientUseCase;
import com.clinicboard.business_service.domain.port.PatientRepositoryPort;
import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import com.clinicboard.business_service.domain.service.PatientRegistrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação dos casos de uso de paciente
 */
@Service
@Transactional
public class PatientUseCaseImpl implements PatientUseCase {
    
    private final PatientRepositoryPort patientRepository;
    private final PatientRegistrationService registrationService;
    private final DomainPatientMapper patientMapper;
    
    public PatientUseCaseImpl(PatientRepositoryPort patientRepository,
                             PatientRegistrationService registrationService,
                             DomainPatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.registrationService = registrationService;
        this.patientMapper = patientMapper;
    }
    
    @Override
    public PatientResponseDto registerPatient(PatientRequestDto request) {
        // Criar value objects
        Contact contact = new Contact(request.getContact(), request.getEmail());
        ProfessionalId professionalId = new ProfessionalId(request.getProfessionalId());
        
        // Validar através do domain service
        registrationService.validatePatientRegistration(contact, professionalId);
        
        // Criar agregado
        Patient patient = new Patient(request.getName(), contact, professionalId);
        
        // Salvar
        Patient savedPatient = patientRepository.save(patient);
        
        return patientMapper.toDto(savedPatient);
    }
    
    @Override
    public PatientResponseDto updatePatient(String patientId, PatientRequestDto request) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new CustomGenericException("Paciente não encontrado"));
        
        Contact newContact = new Contact(request.getContact(), request.getEmail());
        
        // Validar atualização
        registrationService.validatePatientUpdate(patientId, newContact);
        
        // Atualizar usando comportamento do agregado
        patient.updateInformation(request.getName(), newContact);
        
        if (request.getProfessionalId() != null && !request.getProfessionalId().isEmpty()) {
            ProfessionalId newProfessionalId = new ProfessionalId(request.getProfessionalId());
            patient.changeProfessional(newProfessionalId);
        }
        
        Patient savedPatient = patientRepository.save(patient);
        
        return patientMapper.toDto(savedPatient);
    }
    
    @Override
    public void removePatient(String patientId) {
        if (patientRepository.findById(patientId).isEmpty()) {
            throw new CustomGenericException("Paciente não encontrado");
        }
        patientRepository.deleteById(patientId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PatientResponseDto findPatientById(String patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new CustomGenericException("Paciente não encontrado"));
        
        return patientMapper.toDto(patient);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> findAllPatients() {
        return patientRepository.findAll().stream()
            .map(patientMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> findPatientsByProfessional(String professionalId) {
        return patientRepository.findByProfessionalId(professionalId).stream()
            .map(patientMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PatientResponseDto> findPatientsByFilter(String param, String value) {
        // Por simplicidade, vamos implementar apenas busca por nome por enquanto
        // Em um cenário real, poderia ter mais filtros
        switch (param.toLowerCase()) {
            case "name":
                return patientRepository.findAll().stream()
                    .filter(patient -> patient.getName().toLowerCase().contains(value.toLowerCase()))
                    .map(patientMapper::toDto)
                    .collect(Collectors.toList());
            default:
                throw new CustomGenericException("Parâmetro de busca inválido: " + param);
        }
    }
}

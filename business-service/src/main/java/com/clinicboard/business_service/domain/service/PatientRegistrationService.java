package com.clinicboard.business_service.domain.service;

import com.clinicboard.business_service.domain.port.PatientRepositoryPort;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.application.port.outbound.UserService;
import com.clinicboard.business_service.domain.model.valueobjects.Contact;
import com.clinicboard.business_service.domain.model.valueobjects.ProfessionalId;
import org.springframework.stereotype.Service;

/**
 * Serviço de domínio para validações de paciente
 */
@Service
public class PatientRegistrationService {
    
    private final PatientRepositoryPort patientRepository;
    private final UserService userService;
    
    public PatientRegistrationService(PatientRepositoryPort patientRepository, UserService userService) {
        this.patientRepository = patientRepository;
        this.userService = userService;
    }
    
    /**
     * Valida se um paciente pode ser registrado
     */
    public void validatePatientRegistration(Contact contact, ProfessionalId professionalId) {
        // Verifica se email já está em uso
        if (patientRepository.findByEmail(contact.getEmail()).isPresent()) {
            throw new CustomGenericException("Email já cadastrado");
        }
        
        // Verifica se o usuário é um profissional válido
        if (!userService.isUserProfessional(professionalId.getValue())) {
            throw new BusinessException("Perfil não permitido para essa operação");
        }
    }
    
    /**
     * Valida se um paciente pode ser atualizado
     */
    public void validatePatientUpdate(String patientId, Contact newContact) {
        // Verifica se o paciente existe
        if (patientRepository.findById(patientId).isEmpty()) {
            throw new CustomGenericException("Paciente não encontrado");
        }
        
        // Verifica se o novo email já está em uso por outro paciente
        patientRepository.findByEmail(newContact.getEmail())
            .ifPresent(existingPatient -> {
                if (!existingPatient.getId().equals(patientId)) {
                    throw new CustomGenericException("Email já está sendo usado por outro paciente");
                }
            });
    }
}

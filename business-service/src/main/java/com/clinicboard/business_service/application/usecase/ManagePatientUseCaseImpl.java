package com.clinicboard.business_service.application.usecase;

import com.clinicboard.business_service.application.port.in.ManagePatientCommand;
import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.application.port.out.ProfessionalValidationGateway;
import com.clinicboard.business_service.domain.model.Patient;
import lombok.extern.slf4j.Slf4j;
import com.clinicboard.business_service.domain.exception.DomainException;
import com.clinicboard.business_service.domain.exception.PatientBusinessRuleException;
import com.clinicboard.business_service.domain.exception.ProfessionalValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Implementação do caso de uso de gerenciamento de pacientes.
 * 
 * Orquestra operações relacionadas ao ciclo de vida dos pacientes,
 * incluindo registro, atualização e mudanças de status.
 * 
 * Princípios DDD aplicados:
 * - Caso de uso na camada de aplicação
 * - Delegação da lógica de negócio para agregados
 * - Validação de regras de negócio
 */
@Component
@Slf4j
public class ManagePatientUseCaseImpl implements ManagePatientCommand {

    private final PatientRepository patientRepository;
    private final ProfessionalValidationGateway professionalValidationGateway;

    public ManagePatientUseCaseImpl(PatientRepository patientRepository, 
                                   ProfessionalValidationGateway professionalValidationGateway) {
        this.patientRepository = Objects.requireNonNull(patientRepository, "PatientRepository cannot be null");
        this.professionalValidationGateway = Objects.requireNonNull(professionalValidationGateway, "ProfessionalValidationGateway cannot be null");
    }

    @Override
    public CreatePatientResponse createPatient(CreatePatientRequest request) {
        Objects.requireNonNull(request, "CreatePatientRequest cannot be null");
         log.info("Requisição para criar paciente no use-case: name={}, email={}, professionalId={}", 
                request.name(), request.email(), request.professionalId());

        try {
            // 1. Verificar se o profissional existe e está ativo
            if (!professionalValidationGateway.isValidAndActiveProfessional(request.professionalId())) {
                throw ProfessionalValidationException.professionalNotFound(request.professionalId().value());
            }
            
            // 2. Verificar se o email já está em uso
            if (patientRepository.existsByEmail(request.email())) {
                throw new PatientBusinessRuleException("Email already in use: " + request.email().value());
            }

            // 3. Criar o paciente usando o construtor do agregado
            Patient patient = new Patient(
                    request.name(),
                    request.email(),
                    request.contactDetails(),
                    request.professionalId()
            );

            // 4. Persistir o paciente
            Patient savedPatient = patientRepository.save(patient);

            // 5. Retornar resposta
            return CreatePatientResponse.success(
                    savedPatient.getId(),
                    savedPatient.getDomainName(),
                    savedPatient.getEmail(),
                    savedPatient.getContact()
            );

        } catch (PatientBusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("Failed to create patient: " + e.getMessage(), e) {
                @Override
                public String getErrorCode() {
                    return "CREATE_PATIENT_FAILED";
                }
            };
        }
    }

    @Override
    public UpdatePatientResponse updatePatient(UpdatePatientRequest request) {
        Objects.requireNonNull(request, "UpdatePatientRequest cannot be null");

        try {
            // 1. Buscar o paciente existente
            Patient existingPatient = patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new PatientBusinessRuleException("Patient not found with ID: " + request.patientId().value()));

            // 2. Verificar se o novo email já está em uso por outro paciente
            if (request.newEmail() != null && !request.newEmail().equals(existingPatient.getEmail())) {
                if (patientRepository.existsByEmailAndIdNot(request.newEmail(), request.patientId())) {
                    throw new PatientBusinessRuleException("Email already in use by another patient: " + request.newEmail().value());
                }
            }

            // 3. Atualizar as informações do paciente
            Patient updatedPatient = existingPatient;
            
            // Atualizar contato se fornecido
            if (request.newContactDetails() != null) {
                updatedPatient = updatedPatient.updateContactInfo(request.newContactDetails());
            }
            
            // Para email, criamos um novo Patient já que não há método específico
            // (Em um caso real, adicionaríamos um método updateEmail no Patient)
            if (request.newEmail() != null) {
                updatedPatient = new Patient(
                    updatedPatient.getId(),
                    updatedPatient.getDomainName(),
                    request.newEmail(),
                    updatedPatient.getContact(),
                    updatedPatient.getAssignedProfessionalId(),
                    updatedPatient.getStatus(),
                    updatedPatient.getCreatedAt(),
                    java.time.LocalDateTime.now()
                );
            }

            // 4. Persistir as mudanças
            Patient savedPatient = patientRepository.save(updatedPatient);

            // 5. Retornar resposta
            return UpdatePatientResponse.success(savedPatient.getId());

        } catch (PatientBusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("Failed to update patient: " + e.getMessage(), e) {
                @Override
                public String getErrorCode() {
                    return "UPDATE_PATIENT_FAILED";
                }
            };
        }
    }

    @Override
    public DeactivatePatientResponse deactivatePatient(DeactivatePatientRequest request) {
        Objects.requireNonNull(request, "DeactivatePatientRequest cannot be null");

        try {
            // 1. Buscar o paciente
            Patient patient = patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new PatientBusinessRuleException("Patient not found with ID: " + request.patientId().value()));

            // 2. Delegar a desativação para o agregado
            Patient deactivatedPatient = patient.deactivate(request.reason());

            // 3. Persistir a mudança
            Patient savedPatient = patientRepository.save(deactivatedPatient);

            // 4. Retornar resposta
            return DeactivatePatientResponse.success(
                    savedPatient.getId(),
                    request.reason()
            );

        } catch (PatientBusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("Failed to deactivate patient: " + e.getMessage(), e) {
                @Override
                public String getErrorCode() {
                    return "DEACTIVATE_PATIENT_FAILED";
                }
            };
        }
    }
}

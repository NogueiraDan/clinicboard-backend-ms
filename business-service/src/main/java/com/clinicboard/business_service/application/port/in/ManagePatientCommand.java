package com.clinicboard.business_service.application.port.in;

import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.PatientName;
import com.clinicboard.business_service.domain.model.Email;
import com.clinicboard.business_service.domain.model.ContactDetails;
import com.clinicboard.business_service.domain.model.ProfessionalId;

/**
 * Command para gerenciar pacientes no sistema.
 * 
 * Centraliza operações de criação e atualização de pacientes,
 * mantendo a integridade das regras de negócio.
 * 
 * Princípios DDD aplicados:
 * - Comandos expressivos para operações de escrita
 * - Encapsulamento de regras de negócio
 * - Value Objects como parâmetros
 */
public interface ManagePatientCommand {

    /**
     * Registra um novo paciente no sistema.
     * 
     * @param request dados para criação do paciente
     * @return resposta com resultado da operação
     * @throws PatientBusinessRuleException se houver violação de regra
     */
    CreatePatientResponse createPatient(CreatePatientRequest request);

    /**
     * Atualiza dados de um paciente existente.
     * 
     * @param request dados para atualização
     * @return resposta com resultado da operação
     * @throws PatientNotFoundException se o paciente não for encontrado
     * @throws PatientBusinessRuleException se houver violação de regra
     */
    UpdatePatientResponse updatePatient(UpdatePatientRequest request);

    /**
     * Desativa um paciente no sistema.
     * 
     * @param request dados para desativação
     * @return resposta com resultado da operação
     */
    DeactivatePatientResponse deactivatePatient(DeactivatePatientRequest request);

    /**
     * Request para criação de paciente.
     */
    record CreatePatientRequest(
        PatientName name,
        Email email,
        ContactDetails contactDetails,
        ProfessionalId professionalId
    ) {
        public CreatePatientRequest {
            if (name == null) {
                throw new IllegalArgumentException("Nome do paciente não pode ser nulo");
            }
            if (email == null) {
                throw new IllegalArgumentException("Email não pode ser nulo");
            }
            if (contactDetails == null) {
                throw new IllegalArgumentException("Dados de contato não podem ser nulos");
            }
            if (professionalId == null) {
                throw new IllegalArgumentException("ID do profissional responsável não pode ser nulo");
            }
        }

        /**
         * Factory method para criar request.
         */
        public static CreatePatientRequest of(
                PatientName name,
                Email email,
                ContactDetails contactDetails,
                ProfessionalId professionalId) {
            return new CreatePatientRequest(name, email, contactDetails, professionalId);
        }
    }

    /**
     * Response da criação de paciente.
     */
    record CreatePatientResponse(
        PatientId patientId,
        PatientName name,
        Email email,
        ContactDetails contactDetails,
        boolean success,
        String message
    ) {
        /**
         * Factory method para resposta de sucesso.
         */
        public static CreatePatientResponse success(
                PatientId patientId,
                PatientName name,
                Email email,
                ContactDetails contactDetails) {
            return new CreatePatientResponse(
                patientId, name, email, contactDetails, true, "Paciente criado com sucesso"
            );
        }

        /**
         * Factory method para resposta de falha.
         */
        public static CreatePatientResponse failure(String message) {
            return new CreatePatientResponse(
                null, null, null, null, false, message
            );
        }
    }

    /**
     * Request para atualização de paciente.
     */
    record UpdatePatientRequest(
        PatientId patientId,
        Email newEmail,
        ContactDetails newContactDetails
    ) {
        public UpdatePatientRequest {
            if (patientId == null) {
                throw new IllegalArgumentException("PatientId não pode ser nulo");
            }
        }

        /**
         * Factory method para atualização apenas do email.
         */
        public static UpdatePatientRequest withEmail(PatientId patientId, Email email) {
            return new UpdatePatientRequest(patientId, email, null);
        }

        /**
         * Factory method para atualização apenas do contato.
         */
        public static UpdatePatientRequest withContact(PatientId patientId, ContactDetails contact) {
            return new UpdatePatientRequest(patientId, null, contact);
        }
    }

    /**
     * Response da atualização de paciente.
     */
    record UpdatePatientResponse(
        PatientId patientId,
        boolean success,
        String message
    ) {
        /**
         * Factory method para resposta de sucesso.
         */
        public static UpdatePatientResponse success(PatientId patientId) {
            return new UpdatePatientResponse(
                patientId, true, "Paciente atualizado com sucesso"
            );
        }

        /**
         * Factory method para resposta de falha.
         */
        public static UpdatePatientResponse failure(PatientId patientId, String message) {
            return new UpdatePatientResponse(patientId, false, message);
        }
    }

    /**
     * Request para desativação de paciente.
     */
    record DeactivatePatientRequest(
        PatientId patientId,
        String reason
    ) {
        public DeactivatePatientRequest {
            if (patientId == null) {
                throw new IllegalArgumentException("PatientId não pode ser nulo");
            }
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Motivo da desativação é obrigatório");
            }
        }
    }

    /**
     * Response da desativação de paciente.
     */
    record DeactivatePatientResponse(
        PatientId patientId,
        String reason,
        boolean success,
        String message
    ) {
        /**
         * Factory method para resposta de sucesso.
         */
        public static DeactivatePatientResponse success(PatientId patientId, String reason) {
            return new DeactivatePatientResponse(
                patientId, reason, true, "Paciente desativado com sucesso"
            );
        }

        /**
         * Factory method para resposta de falha.
         */
        public static DeactivatePatientResponse failure(PatientId patientId, String message) {
            return new DeactivatePatientResponse(
                patientId, null, false, message
            );
        }
    }
}

package com.clinicboard.business_service.application.port.in;

import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.PatientName;
import com.clinicboard.business_service.domain.model.PatientStatus;
import com.clinicboard.business_service.domain.model.Email;
import com.clinicboard.business_service.domain.model.ContactDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Query para buscar pacientes no sistema.
 * 
 * Centraliza operações de consulta relacionadas a pacientes,
 * fornecendo views otimizadas para leitura.
 * 
 * Princípios DDD aplicados:
 * - Separação entre comandos e queries (CQRS)
 * - Views otimizadas para casos de uso específicos
 * - Queries expressivas na linguagem ubíqua
 */
public interface FindPatientQuery {

    /**
     * Busca um paciente por ID.
     * 
     * @param patientId ID do paciente
     * @return paciente encontrado ou vazio se não existir
     */
    Optional<PatientView> findById(PatientId patientId);

    /**
     * Busca paciente por email.
     * 
     * @param email email do paciente
     * @return paciente encontrado ou vazio se não existir
     */
    Optional<PatientView> findByEmail(Email email);

    /**
     * Busca pacientes por nome (busca parcial).
     * 
     * @param name nome ou parte do nome
     * @return lista de pacientes encontrados
     */
    List<PatientView> findByNameContaining(String name);

    /**
     * Busca pacientes por status.
     * 
     * @param status status do paciente
     * @return lista de pacientes com o status especificado
     */
    List<PatientView> findByStatus(PatientStatus status);

    /**
     * Busca pacientes ativos (status ACTIVE).
     * 
     * @return lista de pacientes ativos
     */
    List<PatientView> findActivePatients();

    /**
     * Verifica se um email já está em uso.
     * 
     * @param email email a ser verificado
     * @return true se o email já estiver em uso
     */
    boolean isEmailInUse(Email email);

    /**
     * Verifica se um email já está em uso por outro paciente.
     * 
     * @param email email a ser verificado
     * @param excludePatientId ID do paciente a ser excluído da verificação
     * @return true se o email já estiver em uso por outro paciente
     */
    boolean isEmailInUseByOtherPatient(Email email, PatientId excludePatientId);

    /**
     * View otimizada para leitura de pacientes.
     * 
     * Representa uma projeção dos dados do paciente
     * otimizada para operações de consulta.
     */
    record PatientView(
        PatientId patientId,
        PatientName name,
        Email email,
        ContactDetails contactDetails,
        PatientStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        /**
         * Factory method para criar view a partir de dados básicos.
         */
        public static PatientView of(
                PatientId patientId,
                PatientName name,
                Email email,
                ContactDetails contactDetails,
                PatientStatus status,
                LocalDateTime createdAt) {
            return new PatientView(
                patientId, name, email, contactDetails, status, createdAt, null
            );
        }

        /**
         * Verifica se o paciente está ativo.
         */
        public boolean isActive() {
            return status == PatientStatus.ACTIVE;
        }

        /**
         * Verifica se o paciente pode ser agendado.
         */
        public boolean canBeScheduled() {
            return isActive();
        }

        /**
         * Retorna o nome formatado do paciente.
         */
        public String getFormattedName() {
            return name.getFormattedName();
        }

        /**
         * Retorna as iniciais do paciente.
         */
        public String getInitials() {
            return name.getInitials();
        }

        /**
         * Retorna informações resumidas do paciente para listagens.
         */
        public String getSummaryInfo() {
            return String.format("%s (%s) - %s", 
                getFormattedName(), 
                email.value(), 
                status.name()
            );
        }
    }
}
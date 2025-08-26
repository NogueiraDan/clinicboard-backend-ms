package com.clinicboard.business_service.application.port.out;

import com.clinicboard.business_service.domain.model.Patient;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.PatientStatus;
import com.clinicboard.business_service.domain.model.Email;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para persistência de pacientes.
 * 
 * Define o contrato que a infraestrutura deve implementar
 * para operações de persistência relacionadas a pacientes.
 * 
 * Princípios DDD aplicados:
 * - Interface na camada de aplicação (Dependency Inversion)
 * - Contrato agnóstico à tecnologia de persistência
 * - Operações orientadas ao domínio
 */
public interface PatientRepository {

    /**
     * Salva um novo paciente ou atualiza um existente.
     * 
     * @param patient paciente a ser persistido
     * @return paciente persistido com ID gerado (se novo)
     */
    Patient save(Patient patient);

    /**
     * Busca um paciente por ID.
     * 
     * @param patientId ID do paciente
     * @return paciente encontrado ou vazio se não existir
     */
    Optional<Patient> findById(PatientId patientId);

    /**
     * Busca paciente por email.
     * 
     * @param email email do paciente
     * @return paciente encontrado ou vazio se não existir
     */
    Optional<Patient> findByEmail(Email email);

    /**
     * Busca pacientes por nome (busca parcial, case-insensitive).
     * 
     * @param name nome ou parte do nome
     * @return lista de pacientes encontrados
     */
    List<Patient> findByNameContaining(String name);

    /**
     * Busca pacientes por status.
     * 
     * @param status status do paciente
     * @return lista de pacientes com o status especificado
     */
    List<Patient> findByStatus(PatientStatus status);

    /**
     * Busca todos os pacientes ativos.
     * 
     * @return lista de pacientes ativos
     */
    List<Patient> findActivePatients();

    /**
     * Verifica se um email já está em uso.
     * 
     * @param email email a ser verificado
     * @return true se o email já estiver em uso
     */
    boolean existsByEmail(Email email);

    /**
     * Verifica se um email já está em uso por outro paciente.
     * 
     * @param email email a ser verificado
     * @param excludePatientId ID do paciente a ser excluído da verificação
     * @return true se o email já estiver em uso por outro paciente
     */
    boolean existsByEmailAndIdNot(Email email, PatientId excludePatientId);

    /**
     * Verifica se um paciente existe.
     * 
     * @param patientId ID do paciente
     * @return true se o paciente existir
     */
    boolean existsById(PatientId patientId);

    /**
     * Remove um paciente do repositório.
     * 
     * @param patientId ID do paciente a ser removido
     */
    void deleteById(PatientId patientId);

    /**
     * Conta o número total de pacientes.
     * 
     * @return número total de pacientes
     */
    long count();

    /**
     * Conta o número de pacientes por status.
     * 
     * @param status status do paciente
     * @return número de pacientes com o status especificado
     */
    long countByStatus(PatientStatus status);

    /**
     * Busca pacientes criados recentemente.
     * 
     * @param limit limite de resultados
     * @return lista dos pacientes mais recentes
     */
    List<Patient> findRecentPatients(int limit);

    /**
     * Busca todos os pacientes (com paginação).
     * 
     * @param page número da página (zero-based)
     * @param size tamanho da página
     * @return lista paginada de pacientes
     */
    List<Patient> findAll(int page, int size);
}

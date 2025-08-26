package com.clinicboard.business_service.application.port.in;

import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentType;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;

/**
 * Command para agendar uma nova consulta.
 * 
 * Representa uma intenção de agendar uma consulta no sistema,
 * encapsulando todos os dados necessários para a operação.
 * 
 * Princípios DDD aplicados:
 * - Value Object como parâmetro de entrada
 * - Comando expressivo na linguagem ubíqua
 * - Imutabilidade para garantir integridade
 */
public interface ScheduleAppointmentCommand {

    /**
     * Agenda uma nova consulta no sistema.
     * 
     * @param request dados necessários para agendar a consulta
     * @return resposta com o resultado do agendamento
     * @throws AppointmentConflictException se houver conflito de horário
     * @throws InvalidTimeSlotException se o horário for inválido
     * @throws PatientBusinessRuleException se o paciente não puder ser agendado
     */
    ScheduleAppointmentResponse scheduleAppointment(ScheduleAppointmentRequest request);

    /**
     * Request contendo os dados para agendamento de consulta.
     */
    record ScheduleAppointmentRequest(
        PatientId patientId,
        ProfessionalId professionalId,
        AppointmentTime appointmentTime,
        AppointmentType appointmentType,
        String observations
    ) {
        public ScheduleAppointmentRequest {
            if (patientId == null) {
                throw new IllegalArgumentException("PatientId não pode ser nulo");
            }
            if (professionalId == null) {
                throw new IllegalArgumentException("ProfessionalId não pode ser nulo");
            }
            if (appointmentTime == null) {
                throw new IllegalArgumentException("AppointmentTime não pode ser nulo");
            }
            if (appointmentType == null) {
                throw new IllegalArgumentException("AppointmentType não pode ser nulo");
            }
        }

        /**
         * Factory method para criar request com dados mínimos obrigatórios.
         */
        public static ScheduleAppointmentRequest of(
                PatientId patientId,
                ProfessionalId professionalId,
                AppointmentTime appointmentTime,
                AppointmentType appointmentType) {
            return new ScheduleAppointmentRequest(
                patientId, 
                professionalId, 
                appointmentTime, 
                appointmentType, 
                null
            );
        }
    }

    /**
     * Response com o resultado do agendamento.
     */
    record ScheduleAppointmentResponse(
        AppointmentId appointmentId,
        PatientId patientId,
        ProfessionalId professionalId,
        AppointmentTime appointmentTime,
        AppointmentType appointmentType,
        String observations,
        boolean success,
        String message
    ) {
        /**
         * Factory method para resposta de sucesso.
         */
        public static ScheduleAppointmentResponse success(
                AppointmentId appointmentId,
                PatientId patientId,
                ProfessionalId professionalId,
                AppointmentTime appointmentTime,
                AppointmentType appointmentType,
                String observations) {
            return new ScheduleAppointmentResponse(
                appointmentId,
                patientId,
                professionalId,
                appointmentTime,
                appointmentType,
                observations,
                true,
                "Consulta agendada com sucesso"
            );
        }

        /**
         * Factory method para resposta de falha.
         */
        public static ScheduleAppointmentResponse failure(String message) {
            return new ScheduleAppointmentResponse(
                null, null, null, null, null, null, false, message
            );
        }
    }
}

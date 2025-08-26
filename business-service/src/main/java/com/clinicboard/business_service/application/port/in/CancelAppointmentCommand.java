package com.clinicboard.business_service.application.port.in;

import com.clinicboard.business_service.domain.model.AppointmentId;

/**
 * Command para cancelar uma consulta existente.
 * 
 * Representa uma intenção de cancelamento de consulta,
 * seguindo o princípio de expressividade da linguagem ubíqua.
 * 
 * Princípios DDD aplicados:
 * - Comando específico para uma operação de negócio
 * - Encapsulamento da lógica de cancelamento
 * - Validações de regras de negócio
 */
public interface CancelAppointmentCommand {

    /**
     * Cancela uma consulta existente no sistema.
     * 
     * @param request dados necessários para cancelamento
     * @return resposta com resultado da operação
     * @throws AppointmentNotFoundException se a consulta não for encontrada
     * @throws InvalidCancellationException se o cancelamento não for permitido
     */
    CancelAppointmentResponse cancelAppointment(CancelAppointmentRequest request);

    /**
     * Request para cancelamento de consulta.
     */
    record CancelAppointmentRequest(
        AppointmentId appointmentId,
        String cancellationReason
    ) {
        public CancelAppointmentRequest {
            if (appointmentId == null) {
                throw new IllegalArgumentException("AppointmentId não pode ser nulo");
            }
            if (cancellationReason == null || cancellationReason.trim().isEmpty()) {
                throw new IllegalArgumentException("Motivo do cancelamento é obrigatório");
            }
        }

        /**
         * Factory method para criar request de cancelamento.
         */
        public static CancelAppointmentRequest of(AppointmentId appointmentId, String reason) {
            return new CancelAppointmentRequest(appointmentId, reason);
        }
    }

    /**
     * Response do cancelamento de consulta.
     */
    record CancelAppointmentResponse(
        AppointmentId appointmentId,
        String cancellationReason,
        boolean success,
        String message
    ) {
        /**
         * Factory method para resposta de sucesso.
         */
        public static CancelAppointmentResponse success(
                AppointmentId appointmentId, 
                String cancellationReason) {
            return new CancelAppointmentResponse(
                appointmentId,
                cancellationReason,
                true,
                "Consulta cancelada com sucesso"
            );
        }

        /**
         * Factory method para resposta de falha.
         */
        public static CancelAppointmentResponse failure(
                AppointmentId appointmentId, 
                String message) {
            return new CancelAppointmentResponse(
                appointmentId, null, false, message
            );
        }
    }
}

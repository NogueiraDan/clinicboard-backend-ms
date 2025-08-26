package com.clinicboard.business_service.domain.exception;

/**
 * Exceção base para todas as violações de regras de negócio no domínio.
 * Representa falhas nas invariantes do domínio de agendamento clínico.
 * 
 * Princípios DDD aplicados:
 * - Exceções de domínio devem expressar conceitos de negócio
 * - Herdam de RuntimeException (unchecked exceptions)
 * - Contêm mensagens em linguagem ubíqua
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Template method para permitir contexto adicional nas exceções filhas
     */
    public abstract String getErrorCode();
}

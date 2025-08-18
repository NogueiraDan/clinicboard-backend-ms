package com.clinicboard.user_service.application.exception;

/**
 * Exceção da camada de aplicação para casos de uso.
 * Representa erros na orquestração de lógica de aplicação.
 */
public class ApplicationException extends RuntimeException {
    
    public ApplicationException(String message) {
        super(message);
    }
    
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

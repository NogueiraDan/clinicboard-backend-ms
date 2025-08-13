package com.clinicboard.user_service.infrastructure.adapter.inbound.web.error;

/**
 * Exceção genérica para erros de infraestrutura
 */
public class CustomGenericException extends RuntimeException {
    
    public CustomGenericException(String message) {
        super(message);
    }
    
    public CustomGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.clinicboard.business_service.common.error;

/**
 * Exceção genérica customizada para validações
 */
public class CustomGenericException extends RuntimeException {
    
    public CustomGenericException(String message) {
        super(message);
    }
    
    public CustomGenericException(String message, Throwable cause) {
        super(message, cause);
    }
}

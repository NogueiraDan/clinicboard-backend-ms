package com.clinicboard.user_service.domain.exception;

/**
 * Exceção de negócio que representa violações de regras de domínio
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

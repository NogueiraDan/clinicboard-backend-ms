package com.clinicboard.user_service.domain.exception;

/**
 * Exceção de domínio que representa violações de regras de negócio.
 * Deve ser usada quando invariantes do domínio são quebradas.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.clinicboard.user_service.common.error;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
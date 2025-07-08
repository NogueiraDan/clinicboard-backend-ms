package com.clinicboard.business_service.common.error;

public class CustomGenericException extends RuntimeException {
    public CustomGenericException(String message) {
        super(message);
    }
}

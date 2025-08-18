package com.clinicboard.user_service.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de erro na camada web.
 * Representa o formato padrão de erro para a API REST.
 */
public class ErrorResponse {
    private boolean error;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.error = true;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, boolean error) {
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

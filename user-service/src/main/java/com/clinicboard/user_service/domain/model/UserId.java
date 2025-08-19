package com.clinicboard.user_service.domain.model;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Value Object que representa um identificador único de usuário.
 * Garante que o ID seja válido conforme as regras de negócio.
 */
public record UserId(String value) {
    
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    public UserId {
        validateId(value);
    }
    
    private static void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId não pode ser nulo ou vazio");
        }
        
        String trimmedId = id.trim();
        
        if (!UUID_PATTERN.matcher(trimmedId).matches()) {
            throw new IllegalArgumentException("UserId deve estar no formato UUID válido");
        }
    }
    
    /**
     * Gera um novo UserId único.
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
    
    /**
     * Retorna o valor do ID trimmed.
     */
    public String getValue() {
        return value.trim();
    }
    
    @Override
    public String toString() {
        return value;
    }
}

package com.clinicboard.user_service.domain.model;

import java.util.regex.Pattern;

/**
 * Value Object que representa o nome de um usuário.
 * Garante que o nome seja válido conforme as regras de negócio.
 */
public record UserName(String value) {
    
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-ZÀ-ÿ\\s]+$");
    
    public UserName {
        validateName(value);
    }
    
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Nome deve ter pelo menos " + MIN_LENGTH + " caracteres");
        }
        
        if (trimmedName.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Nome deve ter no máximo " + MAX_LENGTH + " caracteres");
        }
        
        if (!VALID_NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException("Nome deve conter apenas letras e espaços");
        }
    }
    
    /**
     * Retorna o nome trimmed (sem espaços extras).
     */
    public String trimmedValue() {
        return value.trim();
    }
    
    /**
     * Verifica se o nome é válido para uso como nome de exibição.
     */
    public boolean isDisplayable() {
        return value.trim().length() >= MIN_LENGTH;
    }
}

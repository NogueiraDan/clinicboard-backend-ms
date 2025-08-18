package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.exception.BusinessException;

import java.util.Objects;

/**
 * Value Object que representa uma senha segura.
 * Encapsula regras de validação e comportamentos específicos.
 */
public class Password {
    
    private final String value;
    
    /**
     * Construtor para senhas em texto plano (aplica validação)
     */
    public Password(String value) {
        this(value, false);
    }
    
    /**
     * Construtor interno que permite pular validação para senhas já criptografadas
     */
    private Password(String value, boolean skipValidation) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("Senha não pode ser nula ou vazia");
        }
        
        if (!skipValidation) {
            // Se não parece uma senha BCrypt, validar como texto plano
            if (!value.startsWith("$2a$") && !value.startsWith("$2b$") && !value.startsWith("$2y$")) {
                validatePasswordStrength(value);
            }
        }
        
        this.value = value;
    }
    
    /**
     * Método factory para senhas já criptografadas (pula validação de força)
     */
    public static Password fromEncrypted(String encryptedPassword) {
        return new Password(encryptedPassword, true);
    }
    
    private void validatePasswordStrength(String password) {
        if (password.length() < 6) {
            throw new BusinessException("Senha deve ter no mínimo 6 caracteres");
        }
        
        if (password.length() > 20) {
            throw new BusinessException("Senha deve ter no máximo 20 caracteres");
        }
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "***";
    }
}

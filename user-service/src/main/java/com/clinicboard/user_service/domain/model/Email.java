package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.exception.BusinessException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa um endereço de email válido.
 * Garantindo invariantes do domínio e comportamentos específicos.
 */
public class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private final String value;
    
    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("Email não pode ser nulo ou vazio");
        }
        
        String trimmedValue = value.trim().toLowerCase();
        if (!isValidEmail(trimmedValue)) {
            throw new BusinessException("Formato de email inválido: " + value);
        }
        
        this.value = trimmedValue;
    }
    
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

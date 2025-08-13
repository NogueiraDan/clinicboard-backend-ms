package com.clinicboard.user_service.domain.model.valueobjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa um endereço de email
 * PURO - sem dependências de infraestrutura
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private String value;
    
    public Email(String value) {
        validateEmail(value);
        this.value = value.toLowerCase().trim();
    }
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        
        if (email.length() > 255) {
            throw new IllegalArgumentException("Email não pode ter mais de 255 caracteres");
        }
        
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDomain() {
        return value.substring(value.indexOf("@") + 1);
    }
    
    public String getLocalPart() {
        return value.substring(0, value.indexOf("@"));
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

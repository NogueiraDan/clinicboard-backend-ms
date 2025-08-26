package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa um endereço de email com validações específicas.
 * 
 * Encapsula regras de formatação e validação para emails no contexto clínico.
 */
public record Email(String value) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final int MAX_LENGTH = 254; // RFC 5321

    public Email {
        validateEmail(value);
    }

    private static void validateEmail(String value) {
        if (value == null) {
            throw new InvalidEmailException("Email não pode ser nulo");
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.isEmpty()) {
            throw new InvalidEmailException("Email não pode ser vazio");
        }
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidEmailException(
                String.format("Email deve ter no máximo %d caracteres", MAX_LENGTH)
            );
        }
        
        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidEmailException("Formato de email inválido: " + trimmedValue);
        }
    }

    /**
     * Factory method para criação segura
     */
    public static Email of(String value) {
        return new Email(value);
    }

    /**
     * Retorna o domínio do email
     */
    public String getDomain() {
        int atIndex = value.indexOf('@');
        return atIndex > 0 ? value.substring(atIndex + 1) : "";
    }

    /**
     * Retorna a parte local do email (antes do @)
     */
    public String getLocalPart() {
        int atIndex = value.indexOf('@');
        return atIndex > 0 ? value.substring(0, atIndex) : value;
    }

    /**
     * Verifica se é um email corporativo (domínios conhecidos)
     */
    public boolean isCorporateEmail() {
        String domain = getDomain().toLowerCase();
        return !domain.endsWith("gmail.com") && 
               !domain.endsWith("hotmail.com") && 
               !domain.endsWith("yahoo.com") &&
               !domain.endsWith("outlook.com");
    }

    /**
     * Retorna versão mascarada do email para logs/display
     */
    public String getMaskedEmail() {
        String localPart = getLocalPart();
        String domain = getDomain();
        
        if (localPart.length() <= 2) {
            return "*".repeat(localPart.length()) + "@" + domain;
        }
        
        return localPart.charAt(0) + "*".repeat(localPart.length() - 2) + 
               localPart.charAt(localPart.length() - 1) + "@" + domain;
    }

    /**
     * Exceção específica para violações de email
     */
    public static class InvalidEmailException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_EMAIL";
        
        public InvalidEmailException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}

package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa detalhes de contato (telefone) no contexto clínico.
 * 
 * Encapsula regras de validação e formatação específicas para contatos telefônicos.
 */
public record ContactDetails(String value) {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10,11}$");

    public ContactDetails {
        validateContact(value);
    }

    private static void validateContact(String value) {
        if (value == null) {
            throw new InvalidContactException("Contato não pode ser nulo");
        }
        
        // Remove espaços, parênteses, hífens e outros caracteres
        String cleanedValue = value.replaceAll("[^\\d]", "");
        
        if (cleanedValue.isEmpty()) {
            throw new InvalidContactException("Contato não pode ser vazio");
        }
        
        // Remove código do país se presente (55)
        if (cleanedValue.startsWith("55") && cleanedValue.length() > 11) {
            cleanedValue = cleanedValue.substring(2);
        }
        
        if (!PHONE_PATTERN.matcher(cleanedValue).matches()) {
            throw new InvalidContactException(
                "Contato deve ter entre 10 e 11 dígitos (formato: DDD + número)"
            );
        }
        
        // Valida DDD (primeiros 2 dígitos)
        int ddd = Integer.parseInt(cleanedValue.substring(0, 2));
        if (!isValidDDD(ddd)) {
            throw new InvalidContactException("DDD inválido: " + ddd);
        }
    }

    private static boolean isValidDDD(int ddd) {
        // Lista simplificada de DDDs válidos no Brasil
        return ddd >= 11 && ddd <= 99 && 
               ddd != 20 && ddd != 23 && ddd != 25 && ddd != 26 && ddd != 29 &&
               ddd != 30 && ddd != 36 && ddd != 39 && ddd != 40 && ddd != 50 &&
               ddd != 52 && ddd != 56 && ddd != 57 && ddd != 58 && ddd != 59 &&
               ddd != 70 && ddd != 72 && ddd != 76 && ddd != 78 && ddd != 80 &&
               ddd != 90 && ddd != 93 && ddd != 97 && ddd != 99;
    }

    /**
     * Factory method para criação segura
     */
    public static ContactDetails of(String value) {
        return new ContactDetails(value);
    }

    /**
     * Retorna o contato limpo (apenas números)
     */
    public String getCleanValue() {
        String cleaned = value.replaceAll("[^\\d]", "");
        if (cleaned.startsWith("55") && cleaned.length() > 11) {
            cleaned = cleaned.substring(2);
        }
        return cleaned;
    }

    /**
     * Retorna o contato formatado para exibição
     */
    public String getFormattedValue() {
        String clean = getCleanValue();
        
        if (clean.length() == 10) {
            // Formato: (11) 1234-5678
            return String.format("(%s) %s-%s", 
                clean.substring(0, 2), 
                clean.substring(2, 6), 
                clean.substring(6));
        } else if (clean.length() == 11) {
            // Formato: (11) 91234-5678
            return String.format("(%s) %s-%s", 
                clean.substring(0, 2), 
                clean.substring(2, 7), 
                clean.substring(7));
        }
        
        return clean;
    }

    /**
     * Retorna apenas o DDD
     */
    public String getDDD() {
        String clean = getCleanValue();
        return clean.length() >= 2 ? clean.substring(0, 2) : "";
    }

    /**
     * Verifica se é celular (9 dígitos após DDD)
     */
    public boolean isMobilePhone() {
        return getCleanValue().length() == 11;
    }

    /**
     * Verifica se é telefone fixo (8 dígitos após DDD)
     */
    public boolean isLandLine() {
        return getCleanValue().length() == 10;
    }

    /**
     * Retorna versão mascarada para logs/display
     */
    public String getMaskedValue() {
        String formatted = getFormattedValue();
        if (formatted.length() > 8) {
            return formatted.substring(0, formatted.length() - 4) + "****";
        }
        return "****";
    }

    /**
     * Exceção específica para violações de contato
     */
    public static class InvalidContactException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_CONTACT";
        
        public InvalidContactException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}

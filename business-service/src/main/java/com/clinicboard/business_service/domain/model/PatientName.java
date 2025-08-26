package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa o nome de um paciente com suas validações específicas.
 * 
 * Encapsula regras de formatação e validação para nomes no contexto clínico.
 */
public record PatientName(String value) {
    
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile(
        "^[a-zA-ZÀ-ÿ\\s'.-]+$"
    );

    public PatientName {
        validatePatientName(value);
    }

    private static void validatePatientName(String value) {
        if (value == null) {
            throw new InvalidPatientNameException("Nome do paciente não pode ser nulo");
        }
        
        String trimmedValue = value.trim();
        
        if (trimmedValue.isEmpty()) {
            throw new InvalidPatientNameException("Nome do paciente não pode ser vazio");
        }
        
        if (trimmedValue.length() < MIN_LENGTH) {
            throw new InvalidPatientNameException(
                String.format("Nome do paciente deve ter pelo menos %d caracteres", MIN_LENGTH)
            );
        }
        
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new InvalidPatientNameException(
                String.format("Nome do paciente deve ter no máximo %d caracteres", MAX_LENGTH)
            );
        }
        
        if (!VALID_NAME_PATTERN.matcher(trimmedValue).matches()) {
            throw new InvalidPatientNameException(
                "Nome do paciente contém caracteres inválidos. Apenas letras, espaços, pontos, hífens e apostrofes são permitidos"
            );
        }
    }

    /**
     * Factory method para criação segura
     */
    public static PatientName of(String value) {
        return new PatientName(value);
    }

    /**
     * Retorna o nome formatado (primeira letra de cada palavra maiúscula)
     */
    public String getFormattedName() {
        String[] words = value.trim().split("\\s+");
        StringBuilder formatted = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                formatted.append(" ");
            }
            
            String word = words[i].toLowerCase();
            if (word.length() > 0) {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    formatted.append(word.substring(1));
                }
            }
        }
        
        return formatted.toString();
    }

    /**
     * Retorna as iniciais do nome
     */
    public String getInitials() {
        String[] words = value.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(Character.toUpperCase(word.charAt(0)));
                initials.append(".");
            }
        }
        
        return initials.toString();
    }

    /**
     * Retorna apenas o primeiro nome
     */
    public String getFirstName() {
        String[] words = value.trim().split("\\s+");
        return words.length > 0 ? words[0] : "";
    }

    /**
     * Retorna apenas o último nome
     */
    public String getLastName() {
        String[] words = value.trim().split("\\s+");
        return words.length > 1 ? words[words.length - 1] : words.length > 0 ? words[0] : "";
    }

    /**
     * Exceção específica para violações de nome de paciente
     */
    public static class InvalidPatientNameException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_PATIENT_NAME";
        
        public InvalidPatientNameException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}

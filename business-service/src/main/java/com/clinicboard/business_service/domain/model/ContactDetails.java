package com.clinicboard.business_service.domain.model;

import com.clinicboard.business_service.domain.exception.DomainException;
import java.util.regex.Pattern;

/**
 * Value Object que representa detalhes de contato telefônico no formato internacional brasileiro.
 * 
 * Aceita exclusivamente o formato: +5511999999999 (código país + DDD + número)
 * Encapsula regras rigorosas de validação para garantir consistência.
 */
public record ContactDetails(String value) {
    
    // Padrão para formato internacional brasileiro: +55 + DDD (2 dígitos) + número (8 ou 9 dígitos)
    private static final Pattern INTERNATIONAL_BRAZIL_PATTERN = 
        Pattern.compile("^\\+55[1-9][1-9]\\d{8,9}$");

    public ContactDetails {
        validateContact(value);
    }

    private static void validateContact(String value) {
        if (value == null) {
            throw new InvalidContactException("Contato não pode ser nulo");
        }
        
        if (value.trim().isEmpty()) {
            throw new InvalidContactException("Contato não pode ser vazio");
        }
        
        if (!INTERNATIONAL_BRAZIL_PATTERN.matcher(value).matches()) {
            throw new InvalidContactException(
                "Contato deve estar no formato internacional brasileiro: +5511999999999 " +
                "(+55 + DDD + número de 8 ou 9 dígitos)"
            );
        }
        
        // Extrai e valida o DDD
        String dddStr = value.substring(3, 5); // Posições 3 e 4 (+55[11])
        int ddd = Integer.parseInt(dddStr);
        
        if (!isValidDDD(ddd)) {
            throw new InvalidContactException("DDD inválido: " + ddd);
        }
        
        // Validação adicional para celular: deve começar com 9
        String phoneNumber = value.substring(5); // Número após +55 e DDD
        if (phoneNumber.length() == 9 && !phoneNumber.startsWith("9")) {
            throw new InvalidContactException(
                "Números de celular (9 dígitos) devem começar com 9"
            );
        }
    }

    private static boolean isValidDDD(int ddd) {
        // DDDs válidos no Brasil (lista atualizada)
        return switch (ddd) {
            case 11, 12, 13, 14, 15, 16, 17, 18, 19, // São Paulo
                 21, 22, 24, // Rio de Janeiro
                 27, 28, // Espírito Santo
                 31, 32, 33, 34, 35, 37, 38, // Minas Gerais
                 41, 42, 43, 44, 45, 46, // Paraná
                 47, 48, 49, // Santa Catarina
                 51, 53, 54, 55, // Rio Grande do Sul
                 61, // Distrito Federal
                 62, 64, // Goiás
                 63, // Tocantins
                 65, 66, // Mato Grosso
                 67, // Mato Grosso do Sul
                 68, // Acre
                 69, // Rondônia
                 71, 73, 74, 75, 77, // Bahia
                 79, // Sergipe
                 81, 87, // Pernambuco
                 82, // Alagoas
                 83, // Paraíba
                 84, // Rio Grande do Norte
                 85, 88, // Ceará
                 86, 89, // Piauí
                 91, 93, 94, // Pará
                 92, 97, // Amazonas
                 95, // Roraima
                 96, // Amapá
                 98, 99 // Maranhão
                 -> true;
            default -> false;
        };
    }

    /**
     * Factory method para criação segura
     */
    public static ContactDetails of(String value) {
        return new ContactDetails(value);
    }

    /**
     * Retorna o número sem formatação (+55 seguido de DDD e número)
     */
    public String getCleanValue() {
        return value.substring(1); // Remove apenas o '+'
    }

    /**
     * Retorna o número no formato nacional (sem +55)
     */
    public String getNationalFormat() {
        return value.substring(3); // Remove '+55'
    }

    /**
     * Retorna o contato formatado para exibição brasileira
     */
    public String getFormattedValue() {
        String national = getNationalFormat();
        String ddd = national.substring(0, 2);
        String number = national.substring(2);
        
        if (number.length() == 8) {
            // Telefone fixo: (11) 1234-5678
            return String.format("(%s) %s-%s", 
                ddd, 
                number.substring(0, 4), 
                number.substring(4));
        } else if (number.length() == 9) {
            // Celular: (11) 91234-5678
            return String.format("(%s) %s-%s", 
                ddd, 
                number.substring(0, 5), 
                number.substring(5));
        }
        
        return national;
    }

    /**
     * Retorna apenas o DDD
     */
    public String getDDD() {
        return value.substring(3, 5);
    }

    /**
     * Retorna o código do país (+55)
     */
    public String getCountryCode() {
        return "+55";
    }

    /**
     * Verifica se é celular (9 dígitos após DDD)
     */
    public boolean isMobilePhone() {
        return getNationalFormat().length() == 11; // DDD (2) + número (9)
    }

    /**
     * Verifica se é telefone fixo (8 dígitos após DDD)
     */
    public boolean isLandLine() {
        return getNationalFormat().length() == 10; // DDD (2) + número (8)
    }

    /**
     * Retorna versão mascarada para logs/display
     */
    public String getMaskedValue() {
        if (value.length() > 8) {
            return value.substring(0, value.length() - 4) + "****";
        }
        return "+55********";
    }

    /**
     * Converte para formato de discagem internacional
     */
    public String toInternationalDialingFormat() {
        return value.replace("+", "00"); // +5511999999999 -> 005511999999999
    }

    /**
     * Valida se o número pode receber WhatsApp (celulares brasileiros)
     */
    public boolean isWhatsAppEligible() {
        return isMobilePhone(); // Apenas celulares podem ter WhatsApp
    }

    /**
     * Exceção específica para violações de contato
     */
    public static class InvalidContactException extends DomainException {
        
        private static final String ERROR_CODE = "INVALID_CONTACT_FORMAT";
        
        public InvalidContactException(String message) {
            super(message);
        }

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }
    }
}
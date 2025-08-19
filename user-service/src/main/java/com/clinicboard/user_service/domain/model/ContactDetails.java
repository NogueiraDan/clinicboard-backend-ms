package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.exception.BusinessException;

import java.util.Objects;

/**
 * Value Object que representa detalhes de contato de um usuário no contexto clínico.
 * Encapsula regras de validação específicas do domínio de saúde.
 */
public class ContactDetails {
    
    private final String value;
    
    public ContactDetails(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("Detalhes de contato não podem ser nulos ou vazios");
        }
        
        String cleanedValue = value.trim().replaceAll("[^0-9+]", "");

        // Permite formato internacional: +5583987654321
        if (cleanedValue.startsWith("+")) {
            if (!cleanedValue.matches("^\\+55\\d{11}$")) {
            throw new BusinessException("Contato internacional deve estar no formato +5583987654321");
            }
        } else {
            // Permite apenas números nacionais com 10 ou 11 dígitos
            cleanedValue = cleanedValue.replaceAll("[^0-9]", "");
            if (cleanedValue.length() < 10 || cleanedValue.length() > 11) {
            throw new BusinessException("Contato deve ter entre 10 e 11 dígitos");
            }
        }
        
        this.value = cleanedValue;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getFormattedValue() {
        if (value.length() == 10) {
            return String.format("(%s) %s-%s", 
                value.substring(0, 2), 
                value.substring(2, 6), 
                value.substring(6));
        } else {
            return String.format("(%s) %s-%s", 
                value.substring(0, 2), 
                value.substring(2, 7), 
                value.substring(7));
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactDetails that = (ContactDetails) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return getFormattedValue();
    }
}

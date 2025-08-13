package com.clinicboard.user_service.domain.model.valueobjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa informações de contato
 * PURO - sem dependências de infraestrutura
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contact {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(?\\d{2}\\)?[\\s-]?\\d{4,5}[\\s-]?\\d{4}$"
    );
    
    private String value;
    
    public Contact(String value) {
        validateContact(value);
        this.value = normalizePhone(value);
    }
    
    private void validateContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Contato não pode ser vazio");
        }
        
        String normalizedContact = contact.replaceAll("[\\s()-]", "");
        if (normalizedContact.length() < 10 || normalizedContact.length() > 11) {
            throw new IllegalArgumentException("Contato deve ter entre 10 e 11 dígitos");
        }
        
        if (!PHONE_PATTERN.matcher(contact.trim()).matches()) {
            throw new IllegalArgumentException("Formato de contato inválido");
        }
    }
    
    private String normalizePhone(String phone) {
        // Remove caracteres não numéricos
        String numbersOnly = phone.replaceAll("\\D", "");
        
        // Formata como (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
        if (numbersOnly.length() == 11) {
            return String.format("(%s) %s-%s", 
                numbersOnly.substring(0, 2),
                numbersOnly.substring(2, 7),
                numbersOnly.substring(7));
        } else if (numbersOnly.length() == 10) {
            return String.format("(%s) %s-%s", 
                numbersOnly.substring(0, 2),
                numbersOnly.substring(2, 6),
                numbersOnly.substring(6));
        }
        
        return phone;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getAreaCode() {
        String numbersOnly = value.replaceAll("\\D", "");
        return numbersOnly.substring(0, 2);
    }
    
    public String getNumberWithoutAreaCode() {
        String numbersOnly = value.replaceAll("\\D", "");
        return numbersOnly.substring(2);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(value, contact.value);
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

package com.clinicboard.business_service.domain.model.valueobjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Value Object que representa informações de contato
 * PURO - sem dependências de infraestrutura
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contact {
    
    private String phone;
    private String email;
    
    public Contact(String phone, String email) {
        validatePhone(phone);
        validateEmail(email);
        this.phone = phone;
        this.email = email;
    }
    
    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }
        // Validação básica - aceita formatos diversos
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (cleanPhone.length() < 10 || cleanPhone.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter 10 ou 11 dígitos");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email deve ter formato válido");
        }
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(phone, contact.phone) && 
               Objects.equals(email, contact.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(phone, email);
    }
    
    @Override
    public String toString() {
        return "Contact{phone='" + phone + "', email='" + email + "'}";
    }
}

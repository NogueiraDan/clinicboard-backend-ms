package com.clinicboard.business_service.domain.model.valueobjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Value Object que representa um ID de profissional
 * PURO - sem dependências de infraestrutura
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfessionalId {
    
    private String value;
    
    public ProfessionalId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do profissional não pode ser vazio");
        }
        this.value = value.trim();
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfessionalId that = (ProfessionalId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "ProfessionalId{value='" + value + "'}";
    }
}

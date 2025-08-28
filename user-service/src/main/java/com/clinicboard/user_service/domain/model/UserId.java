package com.clinicboard.user_service.domain.model;

import java.util.UUID;

/**
 * Value Object que representa um identificador único de usuário.
 * Garante que o ID seja válido conforme as regras de negócio.
 */
public record UserId(String value) {

    public UserId {
        validateId(value);
    }

    private static void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId não pode ser nulo ou vazio");
        }

    }

    /**
     * Gera um novo UserId único.
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    /**
     * Retorna o valor do ID trimmed.
     */
    public String getValue() {
        return value.trim();
    }

    @Override
    public String toString() {
        return value;
    }
}

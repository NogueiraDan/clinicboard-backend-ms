package com.clinicboard.user_service.domain.model.valueobjects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa uma senha segura
 * PURO - sem dependências de infraestrutura
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private String hashedValue;
    private String salt;
    
    // Construtor para criar nova senha (com validação e hash)
    public Password(String plainPassword) {
        validatePassword(plainPassword);
        this.salt = generateSalt();
        this.hashedValue = hashPassword(plainPassword, this.salt);
    }
    
    // Construtor para reconstruir senha existente (do banco)
    public Password(String hashedValue, String salt) {
        this.hashedValue = hashedValue;
        this.salt = salt;
    }
    
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        if (password.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres");
        }
        
        if (password.length() > 128) {
            throw new IllegalArgumentException("Senha não pode ter mais de 128 caracteres");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                "Senha deve conter pelo menos: 1 letra minúscula, 1 maiúscula, 1 número e 1 caractere especial"
            );
        }
    }
    
    public boolean matches(String plainPassword) {
        if (plainPassword == null) {
            return false;
        }
        
        String hashedInput = hashPassword(plainPassword, this.salt);
        return hashedInput.equals(this.hashedValue);
    }
    
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }
    
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
    
    public String getHashedValue() {
        return hashedValue;
    }
    
    public String getSalt() {
        return salt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(hashedValue, password.hashedValue) &&
               Objects.equals(salt, password.salt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(hashedValue, salt);
    }
    
    @Override
    public String toString() {
        return "***HIDDEN***";
    }
}

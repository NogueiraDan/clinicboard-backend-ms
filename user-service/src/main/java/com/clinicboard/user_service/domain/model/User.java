package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.exception.BusinessException;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Aggregate Root: User
 * Entidade rica que encapsula todas as regras de negócio relacionadas a usuários.
 * Implementa UserDetails para integração com Spring Security.
 */
public class User implements UserDetails {
    
    private UserId id;
    private String name;
    private Email email;
    private Password password;
    private ContactInfo contact;
    private UserRole role;
    
    // Construtor para criação de novos usuários (sem ID)
    public User(String name, Email email, Password password, ContactInfo contact, UserRole role) {
        this.validateName(name);
        this.name = name;
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.password = Objects.requireNonNull(password, "Password não pode ser nula");
        this.contact = Objects.requireNonNull(contact, "Contact não pode ser nulo");
        this.role = Objects.requireNonNull(role, "Role não pode ser nula");
    }
    
    // Construtor para usuários existentes (com ID)
    public User(UserId id, String name, Email email, Password password, ContactInfo contact, UserRole role) {
        this(name, email, password, contact, role);
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo para usuário existente");
    }
    
    // Construtor padrão para frameworks (JPA, etc.)
    protected User() {}
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Nome não pode ser nulo ou vazio");
        }
        if (name.trim().length() < 2) {
            throw new BusinessException("Nome deve ter pelo menos 2 caracteres");
        }
        if (name.trim().length() > 100) {
            throw new BusinessException("Nome deve ter no máximo 100 caracteres");
        }
    }
    
    /**
     * Método de negócio para alterar informações do usuário
     */
    public void updateProfile(String newName, ContactInfo newContact) {
        if (newName != null) {
            validateName(newName);
            this.name = newName.trim();
        }
        if (newContact != null) {
            this.contact = newContact;
        }
    }
    
    /**
     * Método de negócio para alteração de senha
     */
    public void changePassword(Password newPassword) {
        this.password = Objects.requireNonNull(newPassword, "Nova senha não pode ser nula");
    }
    
    /**
     * Método de negócio para verificar se o usuário pode executar operações administrativas
     */
    public boolean canPerformAdminOperations() {
        return role.isAdmin();
    }
    
    /**
     * Método de negócio para verificar se o usuário pode gerenciar pacientes
     */
    public boolean canManagePatients() {
        return role.isProfessional() || role.isAdmin();
    }
    
    // Getters
    public UserId getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public Password getDomainPassword() {
        return password;
    }
    
    public ContactInfo getContact() {
        return contact;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    // Métodos para uso interno (principalmente frameworks)
    public void setId(UserId id) {
        this.id = id;
    }
    
    // Implementação do UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESSIONAL"));
        }
    }
    
    @Override
    public String getPassword() {
        return password != null ? password.getValue() : null;
    }
    
    @Override
    public String getUsername() {
        return email != null ? email.getValue() : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

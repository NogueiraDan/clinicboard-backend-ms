package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.exception.BusinessException;
import java.util.Objects;

/**
 * Aggregate Root: User
 * Entidade rica que encapsula todas as regras de negócio relacionadas a usuários.
 * Implementa UserDetails para integração com Spring Security.
 */
public class User {
    
    private final UserId id;
    private final UserName name;
    private final Email email;
    private final Password password;
    private final ContactInfo contact;
    private final UserRole role;
    
    // Construtor para criação de novos usuários (sem ID)
    public User(String name, Email email, Password password, ContactInfo contact, UserRole role) {
        this.validateName(name);
        this.id = null; // Será definido pela infraestrutura após persistência
        this.name = new UserName(name);
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.password = Objects.requireNonNull(password, "Password não pode ser nula");
        this.contact = Objects.requireNonNull(contact, "Contact não pode ser nulo");
        this.role = Objects.requireNonNull(role, "Role não pode ser nula");
    }
    
    // Construtor para usuários existentes (com ID)
    public User(UserId id, String name, Email email, Password password, ContactInfo contact, UserRole role) {
        this.validateName(name);
        this.id = Objects.requireNonNull(id, "ID não pode ser nulo para usuário existente");
        this.name = new UserName(name);
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.password = Objects.requireNonNull(password, "Password não pode ser nula");
        this.contact = Objects.requireNonNull(contact, "Contact não pode ser nulo");
        this.role = Objects.requireNonNull(role, "Role não pode ser nula");
    }
    
    // Construtor para usar diretamente UserName (novo)
    public User(UserId id, UserName name, Email email, Password password, ContactInfo contact, UserRole role) {
        this.id = id; // Pode ser null para novos usuários
        this.name = Objects.requireNonNull(name, "Name não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.password = Objects.requireNonNull(password, "Password não pode ser nula");
        this.contact = Objects.requireNonNull(contact, "Contact não pode ser nulo");
        this.role = Objects.requireNonNull(role, "Role não pode ser nula");
    }
    
    // Construtor padrão para frameworks (JPA, etc.)
    protected User() {
        this.id = null;
        this.name = new UserName("Default");
        this.email = null;
        this.password = null;
        this.contact = null;
        this.role = null;
    }
    
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
     * Retorna uma nova instância com as informações atualizadas (imutabilidade)
     */
    public User updateProfile(String newName, ContactInfo newContact) {
        UserName updatedName = (newName != null) ? new UserName(newName) : this.name;
        ContactInfo updatedContact = (newContact != null) ? newContact : this.contact;
        
        return new User(this.id, updatedName, this.email, this.password, updatedContact, this.role);
    }
    
    /**
     * Método de negócio para alteração de senha
     * Retorna uma nova instância com a senha atualizada (imutabilidade)
     */
    public User changePassword(Password newPassword) {
        Objects.requireNonNull(newPassword, "Nova senha não pode ser nula");
        return new User(this.id, this.name, this.email, newPassword, this.contact, this.role);
    }
    
    /**
     * Método de negócio para definir o ID após persistência
     * Retorna uma nova instância com o ID definido
     */
    public User withId(UserId newId) {
        Objects.requireNonNull(newId, "ID não pode ser nulo");
        return new User(newId, this.name, this.email, this.password, this.contact, this.role);
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
        return name.value();
    }
    
    public UserName getDomainName() {
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

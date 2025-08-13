package com.clinicboard.user_service.domain.model;

import com.clinicboard.user_service.domain.event.UserActivatedEvent;
import com.clinicboard.user_service.domain.event.UserDeactivatedEvent;
import com.clinicboard.user_service.domain.event.UserPasswordChangedEvent;
import com.clinicboard.user_service.domain.event.UserRegisteredEvent;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.enums.UserStatus;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDateTime;

/**
 * Agregado User - representa um usuário no contexto do sistema
 * PURO - sem dependências de infraestrutura
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends AbstractAggregateRoot<User> {

    private UserId id;
    private String name;
    private Email email;
    private Password password;
    private Contact contact;
    private UserRole role = UserRole.PROFESSIONAL;
    private UserStatus status = UserStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor para criação do agregado
    public User(String name, Email email, Password password, Contact contact, UserRole role) {
        validateName(name);
        validateEmail(email);
        validatePassword(password);
        validateContact(contact);
        
        this.id = UserId.generate(); // Gera ID automaticamente
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role != null ? role : UserRole.PROFESSIONAL;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Registra evento de domínio
        registerEvent(new UserRegisteredEvent(
            this.id.getValue(),
            this.name,
            this.email.getValue(),
            this.role.name()
        ));
    }

    // Construtor para reconstrução a partir da infraestrutura (usado pelos mappers)
    public User(UserId id, String name, Email email, Password password, Contact contact, 
               UserRole role, UserStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.contact = contact;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Comportamentos de domínio
    public void changePassword(String currentPasswordPlain, String newPasswordPlain) {
        // Verificar senha atual
        if (!this.password.matches(currentPasswordPlain)) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }
        
        // Verificar se nova senha é diferente da atual
        if (currentPasswordPlain.equals(newPasswordPlain)) {
            throw new IllegalArgumentException("Nova senha deve ser diferente da atual");
        }
        
        // Criar e validar nova senha
        Password newPassword = new Password(newPasswordPlain);
        validatePassword(newPassword);
        
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
        
        // Registra evento de domínio
        registerEvent(new UserPasswordChangedEvent(
            this.id.getValue(),
            this.email.getValue()
        ));
    }

    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            throw new IllegalStateException("Usuário já está ativo");
        }
        
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
        
        // Registra evento de domínio
        registerEvent(new UserActivatedEvent(
            this.id.getValue(),
            this.email.getValue(),
            this.name
        ));
    }

    public void deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            throw new IllegalStateException("Usuário já está inativo");
        }
        
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
        
        // Registra evento de domínio
        registerEvent(new UserDeactivatedEvent(
            this.id.getValue(),
            this.email.getValue(),
            this.name
        ));
    }

    public void updateProfile(String name, Contact contact) {
        validateName(name);
        validateContact(contact);
        
        this.name = name;
        this.contact = contact;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean authenticate(String rawPassword) {
        if (this.status != UserStatus.ACTIVE) {
            throw new IllegalStateException("Usuário inativo não pode se autenticar");
        }
        
        return this.password.matches(rawPassword);
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isProfessional() {
        return this.role == UserRole.PROFESSIONAL;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // Métodos de validação privados
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Nome não pode ter mais de 100 caracteres");
        }
    }

    private void validateEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }
    }

    private void validatePassword(Password password) {
        if (password == null) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
    }

    private void validateContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contato não pode ser nulo");
        }
    }

    // Getters para os objetos de domínio (necessários para casos de uso)
    public UserId getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Email getEmail() {
        return this.email;
    }
    
    public Password getPassword() {
        return this.password;
    }
    
    public Contact getContact() {
        return this.contact;
    }
    
    public UserRole getRole() {
        return this.role;
    }
    
    public UserStatus getStatus() {
        return this.status;
    }
    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    // Getters para valores dos Value Objects
    public String getIdValue() {
        return this.id != null ? this.id.getValue() : null;
    }

    public String getEmailValue() {
        return this.email != null ? this.email.getValue() : null;
    }

    public String getContactValue() {
        return this.contact != null ? this.contact.getValue() : null;
    }
}

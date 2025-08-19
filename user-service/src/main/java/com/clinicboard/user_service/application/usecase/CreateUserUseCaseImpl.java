package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.CreateUserUseCase;
import com.clinicboard.user_service.application.port.out.PasswordEncoderPort;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.*;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para criação de usuários.
 * Implementa a lógica de aplicação para criar usuários.
 */
@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    
    public CreateUserUseCaseImpl(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }
    
    @Override
    public User createUser(CreateUserCommand command) {
        // Validar se email já existe
        Email email = new Email(command.email());
        if (userRepositoryPort.existsByEmail(email)) {
            throw new BusinessException("Email já cadastrado no sistema");
        }
        
        // Criptografar senha
        String encryptedPassword = passwordEncoderPort.encode(command.password());
        
        // Criar objetos de valor
        Password password = Password.fromEncrypted(encryptedPassword);
        ContactInfo contact = new ContactInfo(command.contact());
        UserRole role = parseUserRole(command.role());
        
        // Criar entidade User
        User user = new User(command.name(), email, password, contact, role);
        
        // Persistir e retornar
        return userRepositoryPort.save(user);
    }
    
    private UserRole parseUserRole(String roleString) {
        if (roleString == null) {
            return UserRole.of(UserRole.RoleType.PROFESSIONAL); // Valor padrão
        }
        
        try {
            return UserRole.fromCode(roleString.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Role inválida: " + roleString);
        }
    }
}

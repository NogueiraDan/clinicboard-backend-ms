package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.CreateUserUseCase;
import com.clinicboard.user_service.application.port.out.PasswordEncoderPort;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.*;
import com.clinicboard.user_service.domain.service.PasswordPolicyDomainService;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para criação de usuários.
 * Implementa a lógica de aplicação para criar usuários.
 */
@Service
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final PasswordPolicyDomainService passwordPolicyDomainService;
    
    public CreateUserUseCaseImpl(UserRepositoryPort userRepositoryPort, 
                                PasswordEncoderPort passwordEncoderPort,
                                PasswordPolicyDomainService passwordPolicyDomainService) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.passwordPolicyDomainService = passwordPolicyDomainService;
    }
    
    @Override
    public User createUser(CreateUserCommand command) {
        // Validar se email já existe
        Email email = new Email(command.email());
        if (userRepositoryPort.existsByEmail(email)) {
            throw new BusinessException("Email já cadastrado no sistema");
        }
        
        // Criar objetos de valor
        UserRole role = parseUserRole(command.role());
        
        // Validar política de senha usando Domain Service
        passwordPolicyDomainService.validatePasswordPolicy(command.password(), role);
        
        // Criptografar senha após validação
        String encryptedPassword = passwordEncoderPort.encode(command.password());
        Password password = Password.fromEncrypted(encryptedPassword);
        ContactDetails contact = new ContactDetails(command.contact());
        
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

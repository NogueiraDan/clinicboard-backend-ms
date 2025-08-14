package com.clinicboard.user_service.domain.service;

import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.valueobjects.Email;

import java.util.List;

/**
 * Domain Service para regras de negócio complexas envolvendo usuários
 * PURO - sem dependências de infraestrutura
 */
public class UserDomainService {
    
    private final UserRepositoryPort userRepository;
    
    public UserDomainService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Valida se um email pode ser usado para registro
     */
    public void validateEmailAvailability(Email email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está sendo usado por outro usuário");
        }
    }
    
    /**
     * Valida regras de negócio para criação de usuário
     */
    public void validateUserCreation(User user) {
        // Valida unicidade do email
        validateEmailAvailability(user.getEmail());
        
        // Outras validações de domínio podem ser adicionadas aqui
        validateBusinessRules(user);
    }
    
    /**
     * Encontra usuários duplicados por email
     */
    public List<User> findDuplicateUsers(Email email) {
        return userRepository.findByEmail(email)
                .map(List::of)
                .orElse(List.of());
    }
    
    /**
     * Valida se um usuário pode ser removido do sistema
     */
    public void validateUserRemoval(User user) {
        if (user.isAdmin()) {
            // Verifica se não é o último admin do sistema
            List<User> admins = userRepository.findByRole("ADMIN");
            if (admins.size() <= 1) {
                throw new IllegalStateException("Não é possível remover o último administrador do sistema");
            }
        }
    }
    
    /**
     * Aplica regras de negócio específicas
     */
    private void validateBusinessRules(User user) {
        // Exemplo: limitar número de usuários por organização
        // Exemplo: validar domínios de email permitidos
        // Outras regras específicas do negócio
        
        if (user.getName() != null && user.getName().toLowerCase().contains("admin") && !user.isAdmin()) {
            throw new IllegalArgumentException("Apenas usuários com perfil ADMIN podem ter 'admin' no nome");
        }
    }
}

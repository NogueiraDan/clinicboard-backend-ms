package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.UpdateUserUseCase;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.application.exception.ApplicationException;
import com.clinicboard.user_service.domain.model.*;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para atualizar usuários.
 */
@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;
    
    public UpdateUserUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }
    
    @Override
    public User updateUser(UpdateUserCommand command) {
        // Buscar usuário existente
        User existingUser = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new ApplicationException("Usuário não encontrado com o id: " + command.id().getValue()));
        
        // Atualizar campos usando o método correto do domínio (retorna nova instância)
        User updatedUser = existingUser.updateProfile(
            command.name(),
            new ContactInfo(command.contact())
        );
        
        return userRepositoryPort.save(updatedUser);
    }
}

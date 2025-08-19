package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.UpdateUserUseCase;
import com.clinicboard.user_service.application.port.out.UserPersistencePort;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.*;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para atualizar usuários.
 */
@Service
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {
    
    private final UserPersistencePort userPersistencePort;

    public UpdateUserUseCaseImpl(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }    @Override
    public User updateUser(UpdateUserCommand command) {
        // Buscar usuário existente
        User existingUser = userPersistencePort.findById(command.id())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o id: " + command.id().getValue()));
        
        // Atualizar campos usando o método correto do domínio (retorna nova instância)
        User updatedUser = existingUser.updateProfile(
            command.name(),
            new ContactDetails(command.contact())
        );
        
        return userPersistencePort.save(updatedUser);
    }
}

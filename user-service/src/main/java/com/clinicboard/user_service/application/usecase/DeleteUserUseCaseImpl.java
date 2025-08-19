package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.DeleteUserUseCase;
import com.clinicboard.user_service.application.port.out.UserPersistencePort;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.UserId;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para remoção de usuários.
 */
@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {
    
    private final UserPersistencePort userPersistencePort;

    public DeleteUserUseCaseImpl(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }    @Override
    public void deleteUser(UserId id) {
        if (!userPersistencePort.existsById(id)) {
            throw new BusinessException("Usuário não encontrado com o id: " + id.getValue());
        }
        
        userPersistencePort.deleteById(id);
    }
}

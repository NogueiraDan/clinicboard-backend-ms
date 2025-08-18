package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.DeleteUserUseCase;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.application.exception.ApplicationException;
import com.clinicboard.user_service.domain.model.UserId;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para remoção de usuários.
 */
@Service
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;
    
    public DeleteUserUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }
    
    @Override
    public void deleteUser(UserId id) {
        if (!userRepositoryPort.existsById(id)) {
            throw new ApplicationException("Usuário não encontrado com o id: " + id.getValue());
        }
        
        userRepositoryPort.deleteById(id);
    }
}

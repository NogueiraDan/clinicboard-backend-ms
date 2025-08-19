package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.FindUserUseCase;
import com.clinicboard.user_service.application.port.out.UserPersistencePort;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.Email;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para buscar usuários.
 */
@Service
public class FindUserUseCaseImpl implements FindUserUseCase {
    
    private final UserPersistencePort userPersistencePort;

    public FindUserUseCaseImpl(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }    @Override
    public User findById(UserId id) {
        return userPersistencePort.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o id: " + id.getValue()));
    }
    
    @Override
    public User findByEmail(String emailValue) {
        Email email = new Email(emailValue);
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o email: " + emailValue));
    }
}
package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.FindUserUseCase;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
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
    
    private final UserRepositoryPort userRepositoryPort;
    
    public FindUserUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }
    
    @Override
    public User findById(UserId id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o id: " + id.getValue()));
    }
    
    @Override
    public User findByEmail(String emailValue) {
        Email email = new Email(emailValue);
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com o email: " + emailValue));
    }
}
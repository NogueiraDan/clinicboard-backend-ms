package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.ListUsersUseCase;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para listar usuários.
 */
@Service
public class ListUsersUseCaseImpl implements ListUsersUseCase {
    
    private final UserRepositoryPort userRepositoryPort;
    
    public ListUsersUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }
    
    @Override
    public List<User> findAll() {
        return userRepositoryPort.findAll();
    }
}

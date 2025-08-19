package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.ListUsersUseCase;
import com.clinicboard.user_service.application.port.out.UserPersistencePort;
import com.clinicboard.user_service.domain.model.User;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para listar usuários.
 */
@Service
public class ListUsersUseCaseImpl implements ListUsersUseCase {

    private final UserPersistencePort userPersistencePort;

    public ListUsersUseCaseImpl(UserPersistencePort userPersistencePort) {
        this.userPersistencePort = userPersistencePort;
    }
    
    @Override
    public List<User> findAll() {
        return userPersistencePort.findAll();
    }
}

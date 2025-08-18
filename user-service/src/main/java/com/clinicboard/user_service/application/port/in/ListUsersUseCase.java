package com.clinicboard.user_service.application.port.in;

import com.clinicboard.user_service.domain.model.User;

import java.util.List;

/**
 * Porta de entrada para listar usuários.
 */
public interface ListUsersUseCase {
    
    /**
     * Lista todos os usuários do sistema
     */
    List<User> findAll();
}

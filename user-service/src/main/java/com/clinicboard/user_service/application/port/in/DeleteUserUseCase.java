package com.clinicboard.user_service.application.port.in;

import com.clinicboard.user_service.domain.model.UserId;

/**
 * Porta de entrada para remover usuário.
 */
public interface DeleteUserUseCase {
    
    /**
     * Remove um usuário do sistema
     */
    void deleteUser(UserId id);
}

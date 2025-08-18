package com.clinicboard.user_service.application.port.in;

import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;

/**
 * Porta de entrada para atualizar usuário.
 */
public interface UpdateUserUseCase {
    
    /**
     * Atualiza informações de um usuário
     */
    User updateUser(UpdateUserCommand command);
    
    /**
     * Comando para atualização de usuário
     */
    record UpdateUserCommand(
            UserId id,
            String name,
            String contact
    ) {}
}

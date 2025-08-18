package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.port.in.AuthenticateUserUseCase;
import com.clinicboard.user_service.application.port.out.AuthenticationServicePort;
import com.clinicboard.user_service.domain.model.User;

import org.springframework.stereotype.Service;

/**
 * Caso de uso para autenticação de usuários.
 */
@Service
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {
    
    private final AuthenticationServicePort authenticationServicePort;
    
    public AuthenticateUserUseCaseImpl(AuthenticationServicePort authenticationServicePort) {
        this.authenticationServicePort = authenticationServicePort;
    }
    
    @Override
    public AuthenticationResult authenticate(AuthenticationCommand command) {
        User user = authenticationServicePort.authenticate(command.email(), command.password());
        String token = authenticationServicePort.generateToken(user);
        
        return new AuthenticationResult(user, token);
    }
}

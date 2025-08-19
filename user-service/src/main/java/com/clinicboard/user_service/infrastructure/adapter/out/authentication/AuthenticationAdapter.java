package com.clinicboard.user_service.infrastructure.adapter.out.authentication;

import com.clinicboard.user_service.application.port.out.AuthenticationServicePort;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.Email;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.infrastructure.security.TokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Adaptador de infraestrutura para autenticação.
 * Implementa a porta de saída AuthenticationServicePort.
 */
@Component
public class AuthenticationAdapter implements AuthenticationServicePort {
    
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepositoryPort userRepositoryPort;
    
    public AuthenticationAdapter(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserRepositoryPort userRepositoryPort) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepositoryPort = userRepositoryPort;
    }
    
    @Override
    public User authenticate(String email, String password) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(email, password);
        authenticationManager.authenticate(usernamePassword);
        
        // Retornar o usuário do domínio
        Email emailObj = new Email(email);
        return userRepositoryPort.findByEmail(emailObj)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação"));
    }
    
    @Override
    public String generateToken(User user) {
        return tokenService.generateToken(user);
    }
}

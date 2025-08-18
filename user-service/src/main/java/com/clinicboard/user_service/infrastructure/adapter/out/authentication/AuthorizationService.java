package com.clinicboard.user_service.infrastructure.adapter.out.authentication;

import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.Email;
import com.clinicboard.user_service.domain.model.User;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Adaptador de infraestrutura para autorização/carregamento de usuários.
 * Implementa UserDetailsService do Spring Security.
 */
@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    public AuthorizationService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Email email = new Email(username);
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        
        // A entidade User do domínio implementa UserDetails
        return user;
    }
}

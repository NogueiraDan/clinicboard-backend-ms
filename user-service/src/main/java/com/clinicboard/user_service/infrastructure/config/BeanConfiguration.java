package com.clinicboard.user_service.infrastructure.config;

import com.clinicboard.user_service.application.port.out.AuthenticationServicePort;
import com.clinicboard.user_service.application.port.out.UserRepositoryPort;
import com.clinicboard.user_service.infrastructure.adapter.out.authentication.AuthenticationAdapter;
import com.clinicboard.user_service.infrastructure.adapter.out.persistence.UserPersistenceAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * Configuração de beans da aplicação.
 * Define como as dependências são injetadas seguindo a Arquitetura Hexagonal.
 */
@Configuration
public class BeanConfiguration {

    /**
     * Configuração explícita do adaptador de persistência como implementação da porta de saída
     */
    @Bean
    public UserRepositoryPort userRepositoryPort(UserPersistenceAdapter userPersistenceAdapter) {
        return userPersistenceAdapter;
    }

    /**
     * Configuração explícita do adaptador de autenticação como implementação da porta de saída
     */
    @Bean
    public AuthenticationServicePort authenticationServicePort(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserRepositoryPort userRepositoryPort) {
        return new AuthenticationAdapter(authenticationManager, tokenService, userRepositoryPort);
    }
}

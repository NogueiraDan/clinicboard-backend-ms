package com.clinicboard.user_service.infrastructure.config;

import com.clinicboard.user_service.application.port.out.AuthenticationServicePort;
import com.clinicboard.user_service.application.port.out.UserPersistencePort;
import com.clinicboard.user_service.domain.service.PasswordPolicyDomainService;
import com.clinicboard.user_service.infrastructure.adapter.out.authentication.AuthenticationAdapter;
import com.clinicboard.user_service.infrastructure.adapter.out.persistence.UserPersistenceAdapter;
import com.clinicboard.user_service.infrastructure.security.TokenService;

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
    public UserPersistencePort userPersistencePort(UserPersistenceAdapter userPersistenceAdapter) {
        return userPersistenceAdapter;
    }

    /**
     * Configuração explícita do adaptador de autenticação como implementação da porta de saída
     */
    @Bean
    public AuthenticationServicePort authenticationServicePort(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserPersistencePort userPersistencePort) {
        return new AuthenticationAdapter(authenticationManager, tokenService, userPersistencePort);
    }

    /**
     * Domain Service para validação de políticas de senha.
     * Registrado na infraestrutura para manter o domínio agnóstico ao framework.
     */
    @Bean
    public PasswordPolicyDomainService passwordPolicyDomainService() {
        return new PasswordPolicyDomainService();
    }
}

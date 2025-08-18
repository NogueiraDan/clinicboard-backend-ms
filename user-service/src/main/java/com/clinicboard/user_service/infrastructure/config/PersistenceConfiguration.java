package com.clinicboard.user_service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuração da camada de persistência.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.clinicboard.user_service.infrastructure.adapter.out.persistence")
public class PersistenceConfiguration {
}

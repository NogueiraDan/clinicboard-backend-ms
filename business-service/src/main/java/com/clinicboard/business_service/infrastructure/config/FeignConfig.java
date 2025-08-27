package com.clinicboard.business_service.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Feign Client para integração com outros serviços.
 */
@Configuration
@EnableFeignClients(basePackages = "com.clinicboard.business_service.infrastructure.adapter.out.integration.client")
public class FeignConfig {
    // Configurações adicionais do Feign podem ser adicionadas aqui
}

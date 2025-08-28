package com.clinicboard.business_service.infrastructure.adapter.out.integration.client;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client para comunicação com o user-service.
 * 
 * Responsável por realizar chamadas HTTP para o user-service,
 * especificamente para validação de profissionais.
 */
@FeignClient(
    name = "user-service",
    // url = "${app.integration.feign.users:http://localhost:8081}",
    fallback = UserServiceFeignClientFallback.class
)
public interface UserServiceFeignClient {
    
    /**
     * Busca um usuário por ID no user-service.
     * 
     * @param userId ID do usuário a ser buscado
     * @return dados do usuário encontrado
     */
    @GetMapping("/users/{id}")
    Optional<?> findById(@PathVariable("id") String userId);
}

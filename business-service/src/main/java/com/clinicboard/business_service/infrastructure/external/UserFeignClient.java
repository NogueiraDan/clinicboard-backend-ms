package com.clinicboard.business_service.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.clinicboard.business_service.infrastructure.external.dto.UserResponseDto;

/**
 * Cliente Feign para comunicação com o user-service
 * Adaptador de infraestrutura - implementa comunicação HTTP
 */
@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserFeignClient {

    @GetMapping("/users/{id}")
    ResponseEntity<UserResponseDto> findById(@PathVariable String id);
}

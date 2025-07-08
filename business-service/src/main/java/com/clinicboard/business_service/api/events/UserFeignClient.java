package com.clinicboard.business_service.api.events;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.clinicboard.business_service.api.dto.UserResponseDto;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserFeignClient {

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable String id);

}

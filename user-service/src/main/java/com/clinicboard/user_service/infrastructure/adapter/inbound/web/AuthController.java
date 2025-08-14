package com.clinicboard.user_service.infrastructure.adapter.inbound.web;

import com.clinicboard.user_service.application.dto.LoginRequestDto;
import com.clinicboard.user_service.application.dto.LoginResponseDto;
import com.clinicboard.user_service.application.port.inbound.AuthenticationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada - Controller de Autenticação HTTP seguindo a Arquitetura Hexagonal
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    /**
     * Endpoint de login - gera token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        LoginResponseDto response = authenticationUseCase.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para validar token
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        boolean isValid = authenticationUseCase.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}

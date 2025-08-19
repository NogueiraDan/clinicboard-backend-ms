package com.clinicboard.user_service.infrastructure.adapter.in.web;

import com.clinicboard.user_service.application.port.in.AuthenticateUserUseCase;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Adaptador de entrada (Controller) para operações de autenticação.
 * Implementa exclusivamente endpoints de login/autenticação.
 */
@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final AuthWebMapper authWebMapper;
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(
            AuthenticateUserUseCase authenticateUserUseCase,
            AuthWebMapper authWebMapper) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.authWebMapper = authWebMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginDto) {
        AuthenticateUserUseCase.AuthenticationCommand command = authWebMapper.toAuthCommand(loginDto);
        AuthenticateUserUseCase.AuthenticationResult result = authenticateUserUseCase.authenticate(command);
        
        log.info("Autenticando usuário: {}", result.user().getEmail().getValue());
        
        LoginResponseDto responseDto = authWebMapper.toLoginResponseDto(result);
        return ResponseEntity.ok().body(responseDto);
    }
}

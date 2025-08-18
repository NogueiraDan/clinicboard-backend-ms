package com.clinicboard.user_service.infrastructure.adapter.in.web;

import com.clinicboard.user_service.application.port.in.AuthenticateUserUseCase;
import com.clinicboard.user_service.application.port.in.CreateUserUseCase;
import com.clinicboard.user_service.application.port.in.FindUserUseCase;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Adaptador de entrada (Controller) para operações de autenticação.
 * Implementa endpoints de login e registro.
 */
@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UserWebMapper userWebMapper;
    
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(
            AuthenticateUserUseCase authenticateUserUseCase,
            CreateUserUseCase createUserUseCase,
            FindUserUseCase findUserUseCase,
            UserWebMapper userWebMapper) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.userWebMapper = userWebMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginDto) {
        AuthenticateUserUseCase.AuthenticationCommand command = userWebMapper.toAuthCommand(loginDto);
        AuthenticateUserUseCase.AuthenticationResult result = authenticateUserUseCase.authenticate(command);
        
        log.info("Autenticando usuário: {}", result.user().getEmail().getValue());
        
        LoginResponseDto responseDto = userWebMapper.toLoginResponseDto(result);
        return ResponseEntity.ok().body(responseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody @Valid CreateUserRequestDto createDto) {
        // Verificar se email já existe
        try {
            findUserUseCase.findByEmail(createDto.getEmail());
            throw new BusinessException("Email já cadastrado no sistema");
        } catch (Exception e) {
            // Email não existe, pode prosseguir
        }

        CreateUserUseCase.CreateUserCommand command = userWebMapper.toCreateCommand(createDto);
        User savedUser = createUserUseCase.createUser(command);
        UserResponseDto responseDto = userWebMapper.toUserResponseDto(savedUser);

        return ResponseEntity.ok().body(responseDto);
    }
}

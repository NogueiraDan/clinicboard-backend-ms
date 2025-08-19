package com.clinicboard.user_service.infrastructure.adapter.in.web;

import com.clinicboard.user_service.application.port.in.*;
import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;
import com.clinicboard.user_service.infrastructure.adapter.in.web.dto.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Adaptador de entrada (Controller) para operações relacionadas a usuários.
 * Implementa endpoints REST e delega para os casos de uso apropriados.
 */
@RestController
@RequestMapping("users")
public class UserController {

    private final FindUserUseCase findUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final UserWebMapper userWebMapper;

    public UserController(
            FindUserUseCase findUserUseCase,
            ListUsersUseCase listUsersUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            CreateUserUseCase createUserUseCase,
            UserWebMapper userWebMapper) {
        this.findUserUseCase = findUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.userWebMapper = userWebMapper;
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> findAll() {
        List<User> users = listUsersUseCase.findAll();
        List<UserResponseDto> userDtos = users.stream()
                .map(userWebMapper::toUserResponseDto)
                .toList();
        
        return userDtos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable String id) {
        User user = findUserUseCase.findById(new UserId(id));
        UserResponseDto userDto = userWebMapper.toUserResponseDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable String id, 
            @RequestBody @Valid UpdateUserRequestDto updateDto) {
        
        UpdateUserUseCase.UpdateUserCommand command = userWebMapper.toUpdateCommand(id, updateDto);
        User updatedUser = updateUserUseCase.updateUser(command);
        UserResponseDto userDto = userWebMapper.toUserResponseDto(updatedUser);
        
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUserUseCase.deleteUser(new UserId(id));
        return ResponseEntity.noContent().build();
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

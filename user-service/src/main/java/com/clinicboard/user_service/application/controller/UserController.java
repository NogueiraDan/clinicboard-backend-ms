package com.clinicboard.user_service.application.controller;

import org.springframework.web.bind.annotation.RestController;

import com.clinicboard.user_service.api.contract.UserServiceInterface;
import com.clinicboard.user_service.api.dto.UserRequestDto;
import com.clinicboard.user_service.api.dto.UserResponseDto;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserServiceInterface userServiceInterface;

    public UserController(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> findAll() {
        List<UserResponseDto> users = userServiceInterface.findAll();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userServiceInterface.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable String id, @RequestBody UserRequestDto user) {
        return ResponseEntity.status(HttpStatus.OK).body(userServiceInterface.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userServiceInterface.delete(id);
        return ResponseEntity.noContent().build();
    }
}
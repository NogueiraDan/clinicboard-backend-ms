package com.clinicboard.user_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinicboard.user_service.api.contract.UserServiceInterface;
import com.clinicboard.user_service.api.dto.UserRequestDto;
import com.clinicboard.user_service.api.dto.UserResponseDto;
import com.clinicboard.user_service.application.mapper.UserMapper;
import com.clinicboard.user_service.common.Utils;
import com.clinicboard.user_service.common.error.CustomGenericException;
import com.clinicboard.user_service.domain.repository.UserRepository;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserRepository getUserRepository() {
        return this.userRepository;
    }

    @Override
    public UserResponseDto findByEmail(String email) {
        return Utils.convertToUserResponseDto(userRepository.findByEmail(email));
    }

    @Override
    public UserResponseDto save(UserRequestDto user) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserResponseDto findById(String id) {
        return userMapper
                .toDto(userRepository.findById(id)
                        .orElseThrow(() -> new CustomGenericException("Usuário não encontrado com o id: " + id)));
    }

    @Override
    public UserResponseDto update(String id, UserRequestDto userRequestDto) {
        var existingUser = userRepository.findById(id)
                .orElseThrow(() -> new CustomGenericException("Usuário não encontrado com o id: " + id));

        userMapper.updateEntityFromDto(userRequestDto, existingUser);
        return userMapper.toDto(userRepository.save(existingUser));
    }

    @Override
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new CustomGenericException("Usuário não encontrado com o id: " + id);
        }
        userRepository.deleteById(id);
    }

}

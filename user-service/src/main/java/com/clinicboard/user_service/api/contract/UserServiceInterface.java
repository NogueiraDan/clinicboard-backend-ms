package com.clinicboard.user_service.api.contract;

import java.util.List;

import com.clinicboard.user_service.api.dto.UserRequestDto;
import com.clinicboard.user_service.api.dto.UserResponseDto;
import com.clinicboard.user_service.domain.repository.UserRepository;

public interface UserServiceInterface {

    UserResponseDto save(UserRequestDto user);

    UserResponseDto update(String id, UserRequestDto userRequestDto);

    void delete(String id);

    UserResponseDto findById(String id);

    List<UserResponseDto> findAll();

    UserRepository getUserRepository();

    UserResponseDto findByEmail(String email);
}

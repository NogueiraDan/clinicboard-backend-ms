package com.clinicboard.user_service.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import com.clinicboard.user_service.api.dto.UserRequestDto;
import com.clinicboard.user_service.api.dto.UserResponseDto;
import com.clinicboard.user_service.domain.entity.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Mapeamento de UserRequestDto para User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(UserRequestDto userRequestDto);

    UserResponseDto toDto(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateEntityFromDto(UserRequestDto userRequestDto, @MappingTarget User user);
    
}

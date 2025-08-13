package com.clinicboard.user_service.application.mapper;

import com.clinicboard.user_service.application.dto.UserRequestDto;
import com.clinicboard.user_service.application.dto.UserResponseDto;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;

import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre objetos de domínio e DTOs
 */
@Component
public class DomainUserMapper {

    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserResponseDto(
            user.getIdValue(),
            user.getName(),
            user.getEmailValue(),
            user.getContactValue(),
            user.getRole().name(),
            user.getStatus().name(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public User toDomain(UserRequestDto dto) {
        if (dto == null) {
            return null;
        }
        
        return new User(
            dto.getName(),
            new Email(dto.getEmail()),
            new Password(dto.getPassword()),
            new Contact(dto.getContact()),
            UserRole.valueOf(dto.getRole())
        );
    }
}

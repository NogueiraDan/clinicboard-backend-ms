package com.clinicboard.user_service.common;

import org.springframework.security.core.userdetails.UserDetails;

import com.clinicboard.user_service.api.dto.UserResponseDto;
import com.clinicboard.user_service.domain.entity.User;

public class Utils {

    public static UserResponseDto convertToUserResponseDto(UserDetails userDetails) {
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            return new UserResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getContact(),
                    user.getRole());
        }
        return null;
    }

}
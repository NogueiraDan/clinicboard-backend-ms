package com.clinicboard.user_service.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import com.clinicboard.user_service.api.dto.UserRequestDto;
import com.clinicboard.user_service.api.dto.UserResponseDto;
import com.clinicboard.user_service.application.mapper.UserMapper;
import com.clinicboard.user_service.application.service.UserService;
import com.clinicboard.user_service.common.error.CustomGenericException;
import com.clinicboard.user_service.domain.entity.User;
import com.clinicboard.user_service.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private UserDetails userDetails;
    
    private User testUser;
    private UserRequestDto testUserRequestDto;
    private UserResponseDto testUserResponseDto;
    private String testUserId;
    private String testEmail;
    
    @BeforeEach
    void setUp() {
        testUserId = "test-id-123";
        testEmail = "test@example.com";
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail(testEmail);
        testUser.setName("Test User");
        
        testUserRequestDto = new UserRequestDto();
        testUserRequestDto.setEmail(testEmail);
        testUserRequestDto.setName("Test User");
        
        testUserResponseDto = new UserResponseDto();
        testUserResponseDto.setId(testUserId);
        testUserResponseDto.setEmail(testEmail);
        testUserResponseDto.setName("Test User");
    }
    
    @Test
    @DisplayName("Should return UserRepository when getUserRepository is called")
    void getUserRepository_ShouldReturnUserRepository() {
        // Act
        UserRepository result = userService.getUserRepository();
        
        // Assert
        assertNotNull(result);
        assertEquals(userRepository, result);
    }
    
    @Test
    @DisplayName("Should find user by email successfully")
    void findByEmail_WhenUserExists_ShouldReturnUserResponseDto() {
        // Arrange
        when(userRepository.findByEmail(testEmail)).thenReturn(userDetails);
        
        // Act
        UserResponseDto result = userService.findByEmail(testEmail);
        
        // Assert
        verify(userRepository).findByEmail(testEmail);
    }
    
    @Test
    @DisplayName("Should save user successfully")
    void save_WhenValidUserRequestDto_ShouldReturnUserResponseDto() {
        // Arrange
        when(userMapper.toEntity(testUserRequestDto)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserResponseDto);
        
        // Act
        UserResponseDto result = userService.save(testUserRequestDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUserResponseDto, result);
        verify(userMapper).toEntity(testUserRequestDto);
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }
    
    @Test
    @DisplayName("Should find all users successfully")
    void findAll_WhenUsersExist_ShouldReturnListOfUserResponseDto() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(testUser)).thenReturn(testUserResponseDto);
        
        // Act
        List<UserResponseDto> result = userService.findAll();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserResponseDto, result.get(0));
        verify(userRepository).findAll();
        verify(userMapper).toDto(testUser);
    }
    
    @Test
    @DisplayName("Should find user by id successfully")
    void findById_WhenUserExists_ShouldReturnUserResponseDto() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserResponseDto);
        
        // Act
        UserResponseDto result = userService.findById(testUserId);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUserResponseDto, result);
        verify(userRepository).findById(testUserId);
        verify(userMapper).toDto(testUser);
    }
    
    @Test
    @DisplayName("Should throw CustomGenericException when user not found by id")
    void findById_WhenUserNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        CustomGenericException exception = assertThrows(
            CustomGenericException.class,
            () -> userService.findById(testUserId)
        );
        
        assertEquals("Usuário não encontrado com o id: " + testUserId, exception.getMessage());
        verify(userRepository).findById(testUserId);
        verify(userMapper, never()).toDto(any());
    }
    
    @Test
    @DisplayName("Should update user successfully")
    void update_WhenUserExists_ShouldReturnUpdatedUserResponseDto() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserResponseDto);
        
        // Act
        UserResponseDto result = userService.update(testUserId, testUserRequestDto);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUserResponseDto, result);
        verify(userRepository).findById(testUserId);
        verify(userMapper).updateEntityFromDto(testUserRequestDto, testUser);
        verify(userRepository).save(testUser);
        verify(userMapper).toDto(testUser);
    }
    
    @Test
    @DisplayName("Should throw CustomGenericException when updating non-existing user")
    void update_WhenUserNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        CustomGenericException exception = assertThrows(
            CustomGenericException.class,
            () -> userService.update(testUserId, testUserRequestDto)
        );
        
        assertEquals("Usuário não encontrado com o id: " + testUserId, exception.getMessage());
        verify(userRepository).findById(testUserId);
        verify(userMapper, never()).updateEntityFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should delete user successfully")
    void delete_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(testUserId)).thenReturn(true);
        
        // Act
        userService.delete(testUserId);
        
        // Assert
        verify(userRepository).existsById(testUserId);
        verify(userRepository).deleteById(testUserId);
    }
    
    @Test
    @DisplayName("Should throw CustomGenericException when deleting non-existing user")
    void delete_WhenUserNotExists_ShouldThrowCustomGenericException() {
        // Arrange
        when(userRepository.existsById(testUserId)).thenReturn(false);
        
        // Act & Assert
        CustomGenericException exception = assertThrows(
            CustomGenericException.class,
            () -> userService.delete(testUserId)
        );
        
        assertEquals("Usuário não encontrado com o id: " + testUserId, exception.getMessage());
        verify(userRepository).existsById(testUserId);
        verify(userRepository, never()).deleteById(any());
    }
}

package com.clinicboard.user_service.unit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clinicboard.user_service.application.service.AuthorizationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.clinicboard.user_service.api.contract.UserServiceInterface;
import com.clinicboard.user_service.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private UserServiceInterface userServiceInterface;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
    }

    @Test
    @DisplayName("Should load user by username when user exists")
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        when(userServiceInterface.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail(testEmail)).thenReturn(userDetails);

        // Act
        UserDetails result = authorizationService.loadUserByUsername(testEmail);

        // Assert
        assertNotNull(result);
        assertEquals(userDetails, result);
        verify(userServiceInterface).getUserRepository();
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void loadUserByUsername_WhenUserNotExists_ShouldThrowUsernameNotFoundException() {
        // Arrange
        when(userServiceInterface.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail(testEmail)).thenReturn(null);

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authorizationService.loadUserByUsername(testEmail));

        assertEquals("User not found with email: " + testEmail, exception.getMessage());
        verify(userServiceInterface).getUserRepository();
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    @DisplayName("Should call getUserRepository from userServiceInterface")
    void loadUserByUsername_ShouldCallGetUserRepository() {
        // Arrange
        when(userServiceInterface.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail(testEmail)).thenReturn(userDetails);

        // Act
        authorizationService.loadUserByUsername(testEmail);

        // Assert
        verify(userServiceInterface, times(1)).getUserRepository();
    }

}

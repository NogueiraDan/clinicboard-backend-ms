package com.clinicboard.user_service.application.usecase;

import com.clinicboard.user_service.application.dto.UserRequestDto;
import com.clinicboard.user_service.application.dto.UserResponseDto;
import com.clinicboard.user_service.application.mapper.DomainUserMapper;
import com.clinicboard.user_service.application.port.inbound.UserUseCase;
import com.clinicboard.user_service.application.port.outbound.EventPublisher;
import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;
import com.clinicboard.user_service.domain.service.UserDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação dos casos de uso de usuário
 */
@Service
@Transactional
public class UserUseCaseImpl implements UserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final UserDomainService userDomainService;
    private final EventPublisher eventPublisher;
    private final DomainUserMapper userMapper;
    
    public UserUseCaseImpl(UserRepositoryPort userRepository,
                          UserDomainService userDomainService,
                          EventPublisher eventPublisher,
                          DomainUserMapper userMapper) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
        this.eventPublisher = eventPublisher;
        this.userMapper = userMapper;
    }
    
    @Override
    public UserResponseDto registerUser(UserRequestDto request) {
        // Criar value objects
        Email email = new Email(request.getEmail());
        Password password = new Password(request.getPassword());
        Contact contact = new Contact(request.getContact());
        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
        
        // Criar agregado
        User user = new User(
            request.getName(),
            email,
            password,
            contact,
            role
        );
        
        // Validar através do domain service
        userDomainService.validateUserCreation(user);
        
        // Persistir
        User savedUser = userRepository.save(user);
        
        // Publicar evento diretamente
        eventPublisher.publishUserRegistered(
            savedUser.getIdValue(),
            savedUser.getName(),
            savedUser.getEmailValue(),
            savedUser.getRole().name()
        );
        
        return userMapper.toResponseDto(savedUser);
    }
    
    @Override
    public UserResponseDto authenticateUser(String email, String password) {
        Email emailVO = new Email(email);
        
        User user = userRepository.findByEmail(emailVO)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        if (!user.authenticate(password)) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        
        return userMapper.toResponseDto(user);
    }
    
    @Override
    public UserResponseDto findUserById(String userId) {
        UserId id = new UserId(userId);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        return userMapper.toResponseDto(user);
    }
    
    @Override
    public UserResponseDto findUserByEmail(String email) {
        Email emailVO = new Email(email);
        
        User user = userRepository.findByEmail(emailVO)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        return userMapper.toResponseDto(user);
    }
    
    @Override
    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        
        return users.stream()
            .map(userMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserResponseDto> findUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role.toUpperCase());
        
        return users.stream()
            .map(userMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    
    @Override
    public UserResponseDto updateUser(String userId, UserRequestDto request) {
        UserId id = new UserId(userId);
        Contact contact = new Contact(request.getContact());
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Atualizar perfil
        user.updateProfile(request.getName(), contact);
        
        // Persistir
        User updatedUser = userRepository.save(user);
        
        return userMapper.toResponseDto(updatedUser);
    }
    
    @Override
    public void changePassword(String userId, String currentPassword, String newPassword) {
        UserId id = new UserId(userId);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Alterar senha (validação será feita no domínio)
        user.changePassword(currentPassword, newPassword);
        
        // Persistir
        userRepository.save(user);
        
        // Publicar evento diretamente
        eventPublisher.publishUserPasswordChanged(
            user.getIdValue(),
            user.getEmailValue()
        );
    }
    
    @Override
    public void activateUser(String userId) {
        UserId id = new UserId(userId);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        user.activate();
        userRepository.save(user);
        
        // Publicar evento diretamente
        eventPublisher.publishUserActivated(
            user.getIdValue(),
            user.getEmailValue(),
            user.getName()
        );
    }
    
    @Override
    public void deactivateUser(String userId) {
        UserId id = new UserId(userId);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        user.deactivate();
        userRepository.save(user);
        
        // Publicar evento diretamente
        eventPublisher.publishUserDeactivated(
            user.getIdValue(),
            user.getEmailValue(),
            user.getName()
        );
    }
    
    @Override
    public void deleteUser(String userId) {
        UserId id = new UserId(userId);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        
        // Validar se pode ser removido
        userDomainService.validateUserRemoval(user);
        
        userRepository.deleteById(id);
    }
}

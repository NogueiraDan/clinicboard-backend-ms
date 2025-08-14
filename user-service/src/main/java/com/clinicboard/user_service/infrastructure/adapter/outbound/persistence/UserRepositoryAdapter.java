package com.clinicboard.user_service.infrastructure.adapter.outbound.persistence;

import com.clinicboard.user_service.application.port.outbound.UserRepositoryPort;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.mapper.UserPersistenceMapper;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistência - Implementa a porta UserRepositoryPort
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByRole(String role) {
        com.clinicboard.user_service.domain.model.enums.UserRole domainRole = 
            com.clinicboard.user_service.domain.model.enums.UserRole.valueOf(role.toUpperCase());
        
        return userJpaRepository.findByRole(domainRole)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public void deleteById(UserId id) {
        userJpaRepository.deleteById(id.getValue());
    }
}

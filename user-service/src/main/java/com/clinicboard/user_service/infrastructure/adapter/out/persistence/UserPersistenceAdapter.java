package com.clinicboard.user_service.infrastructure.adapter.out.persistence;

import com.clinicboard.user_service.application.port.out.UserPersistencePort;
import com.clinicboard.user_service.domain.model.Email;
import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.UserId;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistência que implementa UserPersistencePort.
 * Faz a ponte entre a aplicação e o banco de dados.
 */
@Component
public class UserPersistenceAdapter implements UserPersistencePort {
    
    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper mapper;
    
    public UserPersistenceAdapter(UserJpaRepository userJpaRepository, UserPersistenceMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toJpaEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return mapper.toDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.getValue())
                .map(mapper::toDomainEntity);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue())
                .map(mapper::toDomainEntity);
    }
    
    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll()
                .stream()
                .map(mapper::toDomainEntity)
                .toList();
    }
    
    @Override
    public boolean existsById(UserId id) {
        return userJpaRepository.existsById(id.getValue());
    }
    
    @Override
    public void deleteById(UserId id) {
        userJpaRepository.deleteById(id.getValue());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }
}

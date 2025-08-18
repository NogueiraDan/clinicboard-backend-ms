package com.clinicboard.user_service.infrastructure.adapter.out.persistence;

import com.clinicboard.user_service.domain.model.*;

import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre entidades de domínio e entidades JPA.
 */
@Component
public class UserPersistenceMapper {
    
    /**
     * Converte User (domínio) para UserJpaEntity (persistência)
     */
    public UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        
        if (user.getId() != null) {
            entity.setId(user.getId().getValue());
        }
        
        entity.setName(user.getName());
        entity.setEmail(user.getEmail().getValue());
        entity.setPassword(user.getDomainPassword().getValue());
        entity.setContact(user.getContact().getValue());
        entity.setRole(user.getRole());
        
        return entity;
    }
    
    /**
     * Converte UserJpaEntity (persistência) para User (domínio)
     */
    public User toDomainEntity(UserJpaEntity entity) {
        UserId id = entity.getId() != null ? new UserId(entity.getId()) : null;
        Email email = new Email(entity.getEmail());
        Password password = new Password(entity.getPassword());
        ContactInfo contact = new ContactInfo(entity.getContact());
        
        User user = new User(entity.getName(), email, password, contact, entity.getRole());
        
        if (id != null) {
            user.setId(id);
        }
        
        return user;
    }
}

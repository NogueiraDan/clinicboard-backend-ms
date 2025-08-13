package com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.mapper;

import com.clinicboard.user_service.domain.model.User;
import com.clinicboard.user_service.domain.model.enums.UserRole;
import com.clinicboard.user_service.domain.model.enums.UserStatus;
import com.clinicboard.user_service.domain.model.valueobjects.Contact;
import com.clinicboard.user_service.domain.model.valueobjects.Email;
import com.clinicboard.user_service.domain.model.valueobjects.Password;
import com.clinicboard.user_service.domain.model.valueobjects.UserId;
import com.clinicboard.user_service.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper manual para conversão entre entidade JPA e domain object
 */
@Component
public class UserPersistenceMapper {
    
    public UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getIdValue());
        entity.setName(user.getName());
        entity.setEmail(user.getEmailValue());
        entity.setPasswordHash(user.getPassword().getHashedValue());
        entity.setPasswordSalt(user.getPassword().getSalt());
        entity.setContact(user.getContactValue());
        entity.setRole(user.getRole());
        entity.setStatus(user.getStatus().name());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
    
    public User toDomain(UserJpaEntity entity) {
        UserId id = new UserId(entity.getId());
        Email email = new Email(entity.getEmail());
        Password password = new Password(entity.getPasswordHash(), entity.getPasswordSalt());
        Contact contact = new Contact(entity.getContact());
        UserRole role = entity.getRole();
        UserStatus status = UserStatus.valueOf(entity.getStatus());
        
        return new User(
            id,
            entity.getName(),
            email,
            password,
            contact,
            role,
            status,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    // Métodos auxiliares já não são necessários - usando enum direto
}

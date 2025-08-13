package com.clinicboard.user_service.infrastructure.adapter.outbound.messaging;

import com.clinicboard.user_service.application.port.outbound.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para publicação de eventos - Implementa a porta EventPublisher
 * Por enquanto apenas loga os eventos, pode ser implementado com RabbitMQ futuramente
 */
@Component
public class EventPublisherAdapter implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherAdapter.class);
    
    @Override
    public void publishUserRegistered(String userId, String name, String email, String role) {
        logger.info("Event Published: UserRegistered - UserId: {}, Name: {}, Email: {}, Role: {}", 
                   userId, name, email, role);
        // TODO: Implementar envio para RabbitMQ
    }
    
    @Override
    public void publishUserActivated(String userId, String email, String name) {
        logger.info("Event Published: UserActivated - UserId: {}, Email: {}, Name: {}", 
                   userId, email, name);
        // TODO: Implementar envio para RabbitMQ
    }
    
    @Override
    public void publishUserDeactivated(String userId, String email, String name) {
        logger.info("Event Published: UserDeactivated - UserId: {}, Email: {}, Name: {}", 
                   userId, email, name);
        // TODO: Implementar envio para RabbitMQ
    }
    
    @Override
    public void publishUserPasswordChanged(String userId, String email) {
        logger.info("Event Published: UserPasswordChanged - UserId: {}, Email: {}", 
                   userId, email);
        // TODO: Implementar envio para RabbitMQ
    }
}

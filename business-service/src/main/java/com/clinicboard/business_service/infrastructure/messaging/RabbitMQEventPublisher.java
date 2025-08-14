package com.clinicboard.business_service.infrastructure.messaging;

import com.clinicboard.business_service.application.port.outbound.EventPublisher;
import com.clinicboard.business_service.application.port.outbound.EventPublishingException;
import com.clinicboard.business_service.domain.event.DomainEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Adaptador para publicação de eventos de domínio via RabbitMQ
 * Implementa o padrão Hexagonal seguindo princípios DDD
 * Com Circuit Breaker e fallback inteligente para DLQ
 */
@Component
public class RabbitMQEventPublisher implements EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${broker.exchange.name}")
    private String exchangeName;
    
    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    @CircuitBreaker(name = "notification-service", fallbackMethod = "publishEventFallback")
    public void publishEvent(final DomainEvent event) {
        try {
            logger.info("Publishing domain event: {} with routing key: {}", 
                    event.getClass().getSimpleName(), event.getRoutingKey());
            
            // Publica no Exchange com Routing Key (não diretamente na fila)
            rabbitTemplate.convertAndSend(
                exchangeName,
                event.getRoutingKey(),
                event
            );
            
            logger.debug("Domain event published successfully: {}", event);
            
        } catch (Exception e) {
            logger.error("Failed to publish domain event: {} - Error: {}", event, e.getMessage(), e);
            throw new EventPublishingException("Failed to publish domain event", e);
        }
    }
    
    /**
     * Fallback quando o serviço está indisponível
     * Os eventos serão automaticamente roteados para DLQ devido à configuração TTL
     */
    public void publishEventFallback(final DomainEvent event, final Exception ex) {
        logger.warn("Circuit breaker activated for event publishing. Event: {} - Error: {}", 
                event.getClass().getSimpleName(), ex.getMessage());
        
        try {
            // Tenta publicar com routing key de falha (será roteado para DLQ)
            String failureRoutingKey = event.getRoutingKey() + ".failed";
            
            rabbitTemplate.convertAndSend(
                exchangeName,
                failureRoutingKey,
                event
            );
            
            logger.info("Event sent to DLQ for later processing: {}", event.getClass().getSimpleName());
            
        } catch (Exception fallbackEx) {
            logger.error("CRITICAL: Failed to send event to DLQ. Event may be lost: {} - Error: {}", 
                     event, fallbackEx.getMessage(), fallbackEx);
            
            // Aqui você pode implementar uma estratégia de último recurso
            // como persistir o evento em banco para retry manual
            persistEventForManualRetry(event, fallbackEx);
        }
    }
    
    private void persistEventForManualRetry(final DomainEvent event, final Exception error) {
        // TODO: Implementar persistência de eventos falhados para retry manual
        // Pode ser uma tabela de outbox events ou um log estruturado
        logger.error("Event persistence for manual retry not implemented yet. Event: {}", event);
        
        // Por enquanto, vamos apenas logar o erro crítico
        logger.error("ALERT: Event lost - Manual intervention required. Event: {} - Error: {}", 
                    event, error.getMessage());
    }
}

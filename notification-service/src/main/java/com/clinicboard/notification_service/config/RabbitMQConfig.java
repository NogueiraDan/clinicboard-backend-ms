package com.clinicboard.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do RabbitMQ para o serviço de notificação.
 * 
 * Esta configuração define:
 * - Exchanges para eventos e DLQ
 * - Filas específicas para cada tipo de evento
 * - Bindings entre exchanges e filas
 * - Conversor JSON para deserialização dos eventos
 * 
 * Alinhada com as configurações do business-service para garantir
 * compatibilidade na comunicação entre os serviços.
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${app.messaging.exchange.events}")
    private String eventsExchange;
    
    @Value("${app.messaging.dlq.exchange}")
    private String dlqExchange;
    
    @Value("${app.messaging.queue.appointment-scheduled}")
    private String appointmentScheduledQueue;
    
    @Value("${app.messaging.queue.appointment-canceled}")
    private String appointmentCanceledQueue;
    
    @Value("${app.messaging.queue.appointment-status-changed}")
    private String appointmentStatusChangedQueue;
    
    @Value("${app.messaging.queue.appointment-rescheduled}")
    private String appointmentRescheduledQueue;
    
    @Value("${app.messaging.queue.dlq}")
    private String dlqQueue;
    
    @Value("${app.messaging.routing-key.appointment-scheduled}")
    private String appointmentScheduledRoutingKey;
    
    @Value("${app.messaging.routing-key.appointment-canceled}")
    private String appointmentCanceledRoutingKey;
    
    @Value("${app.messaging.routing-key.appointment-status-changed}")
    private String appointmentStatusChangedRoutingKey;
    
    @Value("${app.messaging.routing-key.appointment-rescheduled}")
    private String appointmentRescheduledRoutingKey;
    
    @Value("${app.messaging.routing-key.dlq}")
    private String dlqRoutingKey;
    
    // ========== EXCHANGES ==========
    
    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(eventsExchange, true, false);
    }
    
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(dlqExchange, true, false);
    }
    
    // ========== FILAS ==========
    
    @Bean
    public Queue appointmentScheduledQueue() {
        return QueueBuilder.durable(appointmentScheduledQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }
    
    @Bean
    public Queue appointmentCanceledQueue() {
        return QueueBuilder.durable(appointmentCanceledQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }
    
    @Bean
    public Queue appointmentStatusChangedQueue() {
        return QueueBuilder.durable(appointmentStatusChangedQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }
    
    @Bean
    public Queue appointmentRescheduledQueue() {
        return QueueBuilder.durable(appointmentRescheduledQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }
    
    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(dlqQueue).build();
    }
    
    // ========== BINDINGS ==========
    
    @Bean
    public Binding appointmentScheduledBinding() {
        return BindingBuilder
                .bind(appointmentScheduledQueue())
                .to(eventsExchange())
                .with(appointmentScheduledRoutingKey);
    }
    
    @Bean
    public Binding appointmentCanceledBinding() {
        return BindingBuilder
                .bind(appointmentCanceledQueue())
                .to(eventsExchange())
                .with(appointmentCanceledRoutingKey);
    }
    
    @Bean
    public Binding appointmentStatusChangedBinding() {
        return BindingBuilder
                .bind(appointmentStatusChangedQueue())
                .to(eventsExchange())
                .with(appointmentStatusChangedRoutingKey);
    }
    
    @Bean
    public Binding appointmentRescheduledBinding() {
        return BindingBuilder
                .bind(appointmentRescheduledQueue())
                .to(eventsExchange())
                .with(appointmentRescheduledRoutingKey);
    }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder
                .bind(dlqQueue())
                .to(dlqExchange())
                .with(dlqRoutingKey);
    }
    
    // ========== CONVERSOR JSON ==========
    
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        
        // Mapeamento dos eventos do business-service para os eventos locais
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.clinicboard.business_service.domain.event.AppointmentScheduledEvent",
                com.clinicboard.notification_service.domain.event.AppointmentScheduledEvent.class);
        idClassMapping.put("com.clinicboard.business_service.domain.event.AppointmentCanceledEvent",
                com.clinicboard.notification_service.domain.event.AppointmentCanceledEvent.class);
        idClassMapping.put("com.clinicboard.business_service.domain.event.AppointmentStatusChangedEvent",
                com.clinicboard.notification_service.domain.event.AppointmentStatusChangedEvent.class);
        idClassMapping.put("com.clinicboard.business_service.domain.event.AppointmentRescheduledEvent",
                com.clinicboard.notification_service.domain.event.AppointmentRescheduledEvent.class);
        
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }
}

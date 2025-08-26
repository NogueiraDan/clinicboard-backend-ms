package com.clinicboard.business_service.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da infraestrutura de mensageria RabbitMQ.
 * 
 * Define exchanges, filas, bindings e configurações necessárias
 * para publicação e consumo de eventos de domínio.
 */
@Configuration
public class MessagingConfig {

    @Value("${app.messaging.exchanges.business-events:business.events}")
    private String businessEventsExchange;
    
    @Value("${app.messaging.routing-keys.appointment-scheduled:appointment.scheduled}")
    private String appointmentScheduledRoutingKey;
    
    @Value("${app.messaging.routing-keys.appointment-canceled:appointment.canceled}")
    private String appointmentCanceledRoutingKey;
    
    @Value("${app.messaging.routing-keys.appointment-status-changed:appointment.status.changed}")
    private String appointmentStatusChangedRoutingKey;

    /**
     * Exchange principal para eventos de negócio
     */
    @Bean
    public TopicExchange businessEventsExchange() {
        return ExchangeBuilder
                .topicExchange(businessEventsExchange)
                .durable(true)
                .build();
    }

    /**
     * ObjectMapper configurado para serialização de eventos
     */
    @Bean
    public ObjectMapper eventObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Conversor de mensagens JSON para RabbitMQ
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper eventObjectMapper) {
        return new Jackson2JsonMessageConverter(eventObjectMapper);
    }

    /**
     * Template do RabbitMQ configurado
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, 
                                       Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // Log erro de confirmação
                System.err.println("Falha ao confirmar envio da mensagem: " + cause);
            }
        });
        return template;
    }

    /**
     * Container factory para listeners
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}

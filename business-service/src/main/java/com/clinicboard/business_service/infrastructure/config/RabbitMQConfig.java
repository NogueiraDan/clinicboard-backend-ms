package com.clinicboard.business_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {

    @Value("${app.messaging.exchange.events:clinicboard.events}")
    private String eventsExchange;

    @Value("${app.messaging.dlq.exchange:clinicboard.dlq}")
    private String dlqExchange;

    @Value("${app.messaging.dlq.routing-key:events.failed}")
    private String dlqRoutingKey;

    /**
     * Exchange principal para eventos de domínio
     */
    @Bean
    TopicExchange eventsExchange() {
        return new TopicExchange(eventsExchange);
    }

    /**
     * Exchange para Dead Letter Queue (DLQ)
     */
    @Bean
    TopicExchange dlqExchange() {
        return new TopicExchange(dlqExchange);
    }

    /**
     * Fila para Dead Letter Queue
     */
    @Bean
    Queue deadLetterQueue() {
        return new Queue(dlqRoutingKey, true);
    }

    /**
     * Binding da DLQ com o exchange DLQ
     */
    @Bean
    Binding dlqBinding(Queue deadLetterQueue, TopicExchange dlqExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(dlqExchange).with(dlqRoutingKey);
    }

    @Bean
    RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}

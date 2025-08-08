package com.clinicboard.business_service.config;

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
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${broker.queue.notification.name}")
    private String queueName;

    @Value("${broker.queue.notification.dlq.name}")
    private String dlqName;

    @Value("${broker.exchange.name}")
    private String exchangeName;
    
    // Nomes das filas principais
    private static final String APPOINTMENT_SCHEDULED_QUEUE = "appointment.scheduled";
    private static final String APPOINTMENT_CANCELLED_QUEUE = "appointment.cancelled";
    private static final String PATIENT_REGISTERED_QUEUE = "patient.registered";
    
    // Nomes das DLQs (Dead Letter Queues)
    private static final String APPOINTMENT_SCHEDULED_DLQ = "appointment.scheduled.dlq";
    private static final String APPOINTMENT_CANCELLED_DLQ = "appointment.cancelled.dlq";
    private static final String PATIENT_REGISTERED_DLQ = "patient.registered.dlq";
    
    // Configuração da fila principal com DLQ
    @Bean
    Queue queue() {
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", exchangeName);
        args.put("x-dead-letter-routing-key", dlqName);
        args.put("x-message-ttl", 300000); // TTL de 5 minutos
        return new Queue(queueName, true, false, false, args);
    }

    @Bean
    Queue deadLetterQueue() {
        return new Queue(dlqName, true);
    }
    
    // Configuração das filas de agendamento com DLQ
    @Bean
    Queue appointmentScheduledQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", exchangeName);
        args.put("x-dead-letter-routing-key", APPOINTMENT_SCHEDULED_DLQ);
        args.put("x-message-ttl", 300000); // TTL de 5 minutos
        return new Queue(APPOINTMENT_SCHEDULED_QUEUE, true, false, false, args);
    }
    
    @Bean
    Queue appointmentScheduledDLQ() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1800000); // TTL de 30 minutos na DLQ antes do retry
        return new Queue(APPOINTMENT_SCHEDULED_DLQ, true, false, false, args);
    }
    
    @Bean
    Queue appointmentCancelledQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", exchangeName);
        args.put("x-dead-letter-routing-key", APPOINTMENT_CANCELLED_DLQ);
        args.put("x-message-ttl", 300000); // TTL de 5 minutos
        return new Queue(APPOINTMENT_CANCELLED_QUEUE, true, false, false, args);
    }
    
    @Bean
    Queue appointmentCancelledDLQ() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1800000); // TTL de 30 minutos na DLQ
        return new Queue(APPOINTMENT_CANCELLED_DLQ, true, false, false, args);
    }
    
    @Bean
    Queue patientRegisteredQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", exchangeName);
        args.put("x-dead-letter-routing-key", PATIENT_REGISTERED_DLQ);
        args.put("x-message-ttl", 300000); // TTL de 5 minutos
        return new Queue(PATIENT_REGISTERED_QUEUE, true, false, false, args);
    }
    
    @Bean
    Queue patientRegisteredDLQ() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1800000); // TTL de 30 minutos na DLQ
        return new Queue(PATIENT_REGISTERED_DLQ, true, false, false, args);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
    Binding dlqBinding(Queue deadLetterQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deadLetterQueue).to(exchange).with(dlqName);
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

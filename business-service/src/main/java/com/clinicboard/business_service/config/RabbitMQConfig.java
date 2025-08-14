package com.clinicboard.business_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {

    @Value("${broker.exchange.name}")
    private String EXCHANGE_NAME;

    // ==================== ROUTING KEYS (EVENTOS DE DOMÍNIO) ====================
    // Baseadas no contexto de negócio, não em destinos técnicos
    private static final String APPOINTMENT_SCHEDULED_EVENT = "clinic.appointment.scheduled";
    private static final String APPOINTMENT_CANCELLED_EVENT = "clinic.appointment.cancelled";  
    private static final String PATIENT_REGISTERED_EVENT = "clinic.patient.registered";
    
    // Para DLQs - eventos de falha
    private static final String APPOINTMENT_SCHEDULED_FAILED = "clinic.appointment.scheduled.failed";
    private static final String APPOINTMENT_CANCELLED_FAILED = "clinic.appointment.cancelled.failed";
    private static final String PATIENT_REGISTERED_FAILED = "clinic.patient.registered.failed";

    // ==================== NOMES DAS FILAS (DESTINOS TÉCNICOS) ====================
    @Value("${broker.queue.appointment.scheduled}")
    private String APPOINTMENT_SCHEDULED_QUEUE;
    @Value("${broker.queue.appointment.canceled}")
    private String APPOINTMENT_CANCELLED_QUEUE;
    @Value("${broker.queue.patient.registered}")
    private String PATIENT_REGISTERED_QUEUE;

    @Value("${broker.queue.appointment.scheduled.dlq}")
    private String APPOINTMENT_SCHEDULED_DLQ;
    @Value("${broker.queue.appointment.canceled.dlq}")
    private String APPOINTMENT_CANCELLED_DLQ;
    @Value("${broker.queue.patient.registered.dlq}")
    private String PATIENT_REGISTERED_DLQ;

    // ==================== EXCHANGE ====================
    @Bean
    public TopicExchange clinicExchange() {
        return ExchangeBuilder
                .topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    // ==================== FILAS PRINCIPAIS ====================
    @Bean
    public Queue appointmentScheduledQueue() {
        return QueueBuilder
                .durable(APPOINTMENT_SCHEDULED_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", APPOINTMENT_SCHEDULED_FAILED)
                .withArgument("x-message-ttl", 300000) // 5 minutos
                .build();
    }
    
    @Bean
    public Queue appointmentCancelledQueue() {
        return QueueBuilder
                .durable(APPOINTMENT_CANCELLED_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", APPOINTMENT_CANCELLED_FAILED)
                .withArgument("x-message-ttl", 300000)
                .build();
    }
    
    @Bean
    public Queue patientRegisteredQueue() {
        return QueueBuilder
                .durable(PATIENT_REGISTERED_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", PATIENT_REGISTERED_FAILED)
                .withArgument("x-message-ttl", 300000)
                .build();
    }

    // ==================== DLQs ====================
    @Bean
    public Queue appointmentScheduledDLQ() {
        return QueueBuilder
                .durable(APPOINTMENT_SCHEDULED_DLQ)
                .withArgument("x-message-ttl", 1800000) // 30 minutos
                .build();
    }
    
    @Bean
    public Queue appointmentCancelledDLQ() {
        return QueueBuilder
                .durable(APPOINTMENT_CANCELLED_DLQ)
                .withArgument("x-message-ttl", 1800000)
                .build();
    }
    
    @Bean
    public Queue patientRegisteredDLQ() {
        return QueueBuilder
                .durable(PATIENT_REGISTERED_DLQ)
                .withArgument("x-message-ttl", 1800000)
                .build();
    }

    // ==================== BINDINGS PRINCIPAIS ====================
    @Bean
    public Binding appointmentScheduledBinding() {
        return BindingBuilder
                .bind(appointmentScheduledQueue())
                .to(clinicExchange())
                .with(APPOINTMENT_SCHEDULED_EVENT);
    }

    @Bean
    public Binding appointmentCancelledBinding() {
        return BindingBuilder
                .bind(appointmentCancelledQueue())
                .to(clinicExchange())
                .with(APPOINTMENT_CANCELLED_EVENT);
    }

    @Bean
    public Binding patientRegisteredBinding() {
        return BindingBuilder
                .bind(patientRegisteredQueue())
                .to(clinicExchange())
                .with(PATIENT_REGISTERED_EVENT);
    }

    // ==================== BINDINGS DLQ ====================
    @Bean
    public Binding appointmentScheduledDLQBinding() {
        return BindingBuilder
                .bind(appointmentScheduledDLQ())
                .to(clinicExchange())
                .with(APPOINTMENT_SCHEDULED_FAILED);
    }

    @Bean
    public Binding appointmentCancelledDLQBinding() {
        return BindingBuilder
                .bind(appointmentCancelledDLQ())
                .to(clinicExchange())
                .with(APPOINTMENT_CANCELLED_FAILED);
    }

    @Bean
    public Binding patientRegisteredDLQBinding() {
        return BindingBuilder
                .bind(patientRegisteredDLQ())
                .to(clinicExchange())
                .with(PATIENT_REGISTERED_FAILED);
    }

    // ==================== RABBIT TEMPLATE ====================
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
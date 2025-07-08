package com.clinicboard.business_service.api.events;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import com.clinicboard.business_service.api.contract.MessagingInterface;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class RabbitMQProducer implements MessagingInterface {

    final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final DiscoveryClient discoveryClient;

    @Value("${broker.queue.notification.name}")
    private String routingKey;

    
    @Value("${broker.queue.notification.dlq.name}")
    private String dlqRoutingKey;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, DiscoveryClient discoveryClient) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.discoveryClient = discoveryClient;
    }
    private boolean isNotificationServiceAvailable() {
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances("notification-service");
            if (instances != null && !instances.isEmpty()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "sendToDLQ")
    public void publishNotification(AppointmentRequestDto appointment) {
         if (isNotificationServiceAvailable()) {
            System.out.println("Mensagem produzida: " + appointment);
            rabbitTemplate.convertAndSend(routingKey, appointment);
        } else {
            sendToDLQ(appointment, new RuntimeException("NotificationService está fora do ar"));
        }
    }

    public void sendToDLQ(AppointmentRequestDto appointmentRequestDto, Throwable t) {
        System.out
                .println("NotificationService está fora do ar. Enviando mensagem para a DLQ: " + appointmentRequestDto);
        rabbitTemplate.convertAndSend(dlqRoutingKey, appointmentRequestDto);
    }

}

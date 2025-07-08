package com.clinicboard.notification_service.api.events;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.clinicboard.notification_service.api.contract.MessagingInterface;
import com.clinicboard.notification_service.api.dto.AppointmentRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class RabbitMQConsumer implements MessagingInterface {

    final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${broker.queue.notification.name}")
    private String routingKey;

    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @RabbitListener(queues = "${broker.queue.notification.dlq.name}")
    public void receiveMessageFromDLQ(AppointmentRequestDto appointment) {
        System.out.println("Mensagem recebida da DLQ e desserializada: " + appointment.toString());
        // Processar a mensagem da DLQ
    }

    @Override
    @RabbitListener(queues = "${broker.queue.notification.name}")
    public void receiveMessage(AppointmentRequestDto appointment) {
        System.out.println("Mensagem recebida e desserializada: " + appointment.toString());
        // Processar a mensagem da fila principal
    }

}

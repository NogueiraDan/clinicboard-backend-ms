package com.clinicboard.notification_service.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Evento de domínio que representa o agendamento de uma consulta.
 * 
 * Este evento é recebido quando um novo agendamento é criado no business-service
 * e precisa gerar notificações para paciente e profissional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentScheduledEvent implements DomainEvent {
    
    @JsonProperty("aggregateId")
    private String aggregateId;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("professionalId")
    private String professionalId;
    
    @JsonProperty("appointmentDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appointmentDateTime;
    
    @JsonProperty("observation")
    private String observation;
    
    @JsonProperty("patientName")
    private String patientName;
    
    @JsonProperty("professionalName")
    private String professionalName;
    
    @JsonProperty("occurredOn")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private Instant occurredOn;
    
    @Override
    public Instant occurredOn() {
        return this.occurredOn;
    }
    
    @Override
    public String getAggregateId() {
        return this.aggregateId;
    }
}

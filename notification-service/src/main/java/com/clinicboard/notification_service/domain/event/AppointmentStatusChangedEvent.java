package com.clinicboard.notification_service.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Evento de domínio que representa a mudança de status de uma consulta.
 * 
 * Este evento é recebido quando o status de um agendamento muda no business-service
 * e precisa gerar notificações informando a mudança para os interessados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusChangedEvent implements DomainEvent {
    
    @JsonProperty("aggregateId")
    private String aggregateId;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("professionalId")
    private String professionalId;
    
    @JsonProperty("previousStatusName")
    private String previousStatusName;
    
    @JsonProperty("newStatusName")
    private String newStatusName;
    
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

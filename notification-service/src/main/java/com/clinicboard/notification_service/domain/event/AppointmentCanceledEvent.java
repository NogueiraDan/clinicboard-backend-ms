package com.clinicboard.notification_service.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Evento de domínio que representa o cancelamento de uma consulta.
 * 
 * Este evento é recebido quando um agendamento é cancelado no business-service
 * e precisa gerar notificações de cancelamento para paciente e profissional.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCanceledEvent implements DomainEvent {
    
    @JsonProperty("appointmentId")
    private String appointmentId;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("professionalId")
    private String professionalId;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("canceledBy")
    private String canceledBy;
    
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
        return this.appointmentId;
    }
}

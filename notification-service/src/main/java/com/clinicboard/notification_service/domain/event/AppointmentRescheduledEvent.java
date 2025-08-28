package com.clinicboard.notification_service.domain.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Evento de domínio que representa o reagendamento de uma consulta.
 * 
 * Este evento é recebido quando um agendamento é reagendado no business-service
 * e precisa gerar notificações informando as novas datas para os interessados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRescheduledEvent implements DomainEvent {
    
    @JsonProperty("appointmentId")
    private String appointmentId;
    
    @JsonProperty("patientId")
    private String patientId;
    
    @JsonProperty("professionalId")
    private String professionalId;
    
    @JsonProperty("previousDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime previousDateTime;
    
    @JsonProperty("newDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime newDateTime;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("rescheduledBy")
    private String rescheduledBy;
    
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
    
    /**
     * Retorna a data anterior formatada para exibição
     */
    public String getFormattedPreviousTime() {
        return previousDateTime != null ? 
            previousDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
    
    /**
     * Retorna a nova data formatada para exibição
     */
    public String getFormattedNewTime() {
        return newDateTime != null ? 
            newDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "";
    }
}

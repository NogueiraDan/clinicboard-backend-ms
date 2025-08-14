package com.clinicboard.business_service.domain.event;

import java.time.LocalDateTime;

/**
 * Interface base para todos os eventos de domínio
 * Define o contrato comum para eventos que podem ser publicados
 */
public interface DomainEvent {
    
    /**
     * Retorna a routing key para roteamento no message broker
     * A routing key deve representar o evento de negócio, não o destino técnico
     * 
     * @return routing key no formato "contexto.entidade.acao"
     */
    String getRoutingKey();
    
    /**
     * Timestamp de quando o evento ocorreu
     * 
     * @return momento da ocorrência do evento
     */
    LocalDateTime getOccurredAt();
}

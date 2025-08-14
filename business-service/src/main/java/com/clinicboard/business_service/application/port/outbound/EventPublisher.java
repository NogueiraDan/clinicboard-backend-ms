package com.clinicboard.business_service.application.port.outbound;

import com.clinicboard.business_service.domain.event.DomainEvent;

/**
 * Porta de saída para publicação de eventos de domínio
 * Segue os princípios da Arquitetura Hexagonal mantendo a camada de domínio desacoplada
 */
public interface EventPublisher {
    
    /**
     * Publica um evento de domínio de forma assíncrona
     * 
     * @param event Evento de domínio a ser publicado
     * @throws EventPublishingException se falhar ao publicar
     */
    void publishEvent(DomainEvent event);
}

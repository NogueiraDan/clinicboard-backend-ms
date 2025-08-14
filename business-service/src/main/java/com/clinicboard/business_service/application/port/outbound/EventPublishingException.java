package com.clinicboard.business_service.application.port.outbound;

/**
 * Exceção lançada quando há falha na publicação de eventos de domínio
 */
public class EventPublishingException extends RuntimeException {
    
    public EventPublishingException(final String message) {
        super(message);
    }
    
    public EventPublishingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

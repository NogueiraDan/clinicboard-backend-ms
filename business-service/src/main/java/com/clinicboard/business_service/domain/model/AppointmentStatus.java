package com.clinicboard.business_service.domain.model;

/**
 * Value Object que representa o status de um agendamento com suas transições válidas.
 * 
 * Encapsula as regras de mudança de estado específicas do domínio clínico.
 */
public enum AppointmentStatus {
    
    PENDING("Pendente", "Agendamento criado e aguardando confirmação"),
    CONFIRMED("Confirmado", "Agendamento confirmado pelo paciente"),
    SCHEDULED("Agendado", "Agendamento marcado e confirmado"),
    IN_PROGRESS("Em Andamento", "Consulta iniciada"),
    COMPLETED("Concluído", "Consulta finalizada com sucesso"),
    CANCELLED("Cancelado", "Agendamento cancelado"),
    NO_SHOW("Faltou", "Paciente não compareceu"),
    RESCHEDULED("Remarcado", "Agendamento foi remarcado para nova data");

    private final String displayName;
    private final String description;

    AppointmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica se é possível transicionar do status atual para o novo status
     */
    public boolean canTransitionTo(AppointmentStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            
            case CONFIRMED -> newStatus == SCHEDULED || newStatus == CANCELLED || newStatus == RESCHEDULED;
            
            case SCHEDULED -> newStatus == IN_PROGRESS || newStatus == CANCELLED || 
                             newStatus == NO_SHOW || newStatus == RESCHEDULED;
            
            case IN_PROGRESS -> newStatus == COMPLETED || newStatus == CANCELLED;
            
            case COMPLETED, CANCELLED, NO_SHOW -> false; // Estados finais
            
            case RESCHEDULED -> newStatus == PENDING; // Pode voltar para pendente
        };
    }

    /**
     * Verifica se o status representa um agendamento ativo (pode ser atendido)
     */
    public boolean isActive() {
        return this == CONFIRMED || this == SCHEDULED || this == IN_PROGRESS;
    }

    /**
     * Verifica se o status representa um estado final
     */
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    /**
     * Verifica se o agendamento pode ser cancelado
     */
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED || this == SCHEDULED;
    }

    /**
     * Verifica se o agendamento pode ser remarcado
     */
    public boolean isReschedulable() {
        return this == PENDING || this == CONFIRMED || this == SCHEDULED;
    }

    /**
     * Factory method para obter status a partir de string
     */
    public static AppointmentStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return PENDING; // Status padrão
        }
        
        try {
            return AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status de agendamento inválido: " + status);
        }
    }
}

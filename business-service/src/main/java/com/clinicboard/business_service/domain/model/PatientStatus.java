package com.clinicboard.business_service.domain.model;

/**
 * Enum que representa os possíveis status de um paciente no sistema.
 * 
 * Encapsula as regras de transição de estado específicas do domínio clínico.
 */
public enum PatientStatus {
    
    ACTIVE("Ativo", "Paciente ativo no sistema, pode agendar consultas"),
    INACTIVE("Inativo", "Paciente inativo, não pode agendar consultas"),
    SUSPENDED("Suspenso", "Paciente temporariamente suspenso"),
    BLOCKED("Bloqueado", "Paciente bloqueado por violação de políticas");

    private final String displayName;
    private final String description;

    PatientStatus(String displayName, String description) {
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
     * Verifica se é possível transicionar para o novo status
     */
    public boolean canTransitionTo(PatientStatus newStatus) {
        return switch (this) {
            case ACTIVE -> newStatus == INACTIVE || newStatus == SUSPENDED || newStatus == BLOCKED;
            case INACTIVE -> newStatus == ACTIVE;
            case SUSPENDED -> newStatus == ACTIVE || newStatus == BLOCKED;
            case BLOCKED -> newStatus == ACTIVE; // Apenas admin pode desbloquear
        };
    }

    /**
     * Verifica se o paciente pode agendar consultas neste status
     */
    public boolean canScheduleAppointments() {
        return this == ACTIVE;
    }

    /**
     * Verifica se é um status ativo
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Factory method para obter status a partir de string
     */
    public static PatientStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ACTIVE; // Status padrão
        }
        
        try {
            return PatientStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status de paciente inválido: " + status);
        }
    }
}

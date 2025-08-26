package com.clinicboard.business_service.domain.model;

/**
 * Value Object que representa o tipo de agendamento no contexto clínico.
 * 
 * Encapsula regras específicas para diferentes tipos de consulta.
 */
public enum AppointmentType {
    
    FIRST_CONSULTATION("Primeira Consulta", "Consulta inicial do paciente", 60),
    FOLLOW_UP("Retorno", "Consulta de acompanhamento", 30),
    EMERGENCY("Emergência", "Atendimento de emergência", 45),
    PROCEDURE("Procedimento", "Procedimento médico específico", 90),
    EXAM("Exame", "Realização de exame", 30),
    VACCINATION("Vacinação", "Aplicação de vacina", 15),
    TELEMEDICINE("Telemedicina", "Consulta por videoconferência", 30);

    private final String displayName;
    private final String description;
    private final int defaultDurationMinutes;

    AppointmentType(String displayName, String description, int defaultDurationMinutes) {
        this.displayName = displayName;
        this.description = description;
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }

    /**
     * Verifica se é um tipo que requer preparação especial
     */
    public boolean requiresSpecialPreparation() {
        return this == PROCEDURE || this == EXAM;
    }

    /**
     * Verifica se é um tipo que pode ser feito por telemedicina
     */
    public boolean canBeTelemedicine() {
        return this == FOLLOW_UP || this == FIRST_CONSULTATION;
    }

    /**
     * Verifica se é um tipo de urgência
     */
    public boolean isUrgent() {
        return this == EMERGENCY;
    }

    /**
     * Verifica se permite reagendamento
     */
    public boolean allowsRescheduling() {
        return this != EMERGENCY && this != VACCINATION;
    }

    /**
     * Retorna o intervalo mínimo de antecedência necessário (em horas)
     */
    public int getMinimumAdvanceHours() {
        return switch (this) {
            case EMERGENCY -> 0; // Sem antecedência
            case VACCINATION -> 2;
            case FOLLOW_UP -> 4;
            case FIRST_CONSULTATION -> 24;
            case EXAM, PROCEDURE -> 48; // Precisa de mais preparação
            case TELEMEDICINE -> 12;
        };
    }

    /**
     * Verifica se pode ser agendado no mesmo dia
     */
    public boolean canBeSameDayBooking() {
        return this == EMERGENCY || this == VACCINATION || this == FOLLOW_UP;
    }

    /**
     * Factory method para obter tipo a partir de string
     */
    public static AppointmentType fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            return FOLLOW_UP; // Tipo padrão
        }
        
        try {
            return AppointmentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de agendamento inválido: " + type);
        }
    }

    /**
     * Retorna tipos adequados para um contexto específico
     */
    public static AppointmentType[] getTypesForNewPatient() {
        return new AppointmentType[]{FIRST_CONSULTATION, EMERGENCY};
    }

    public static AppointmentType[] getTypesForExistingPatient() {
        return new AppointmentType[]{FOLLOW_UP, PROCEDURE, EXAM, VACCINATION, TELEMEDICINE, EMERGENCY};
    }
}

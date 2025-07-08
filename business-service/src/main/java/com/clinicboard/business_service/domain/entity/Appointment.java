package com.clinicboard.business_service.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "agendamentos")
@Entity(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_agendamento")
    private String id;

    @Column(name = "data", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private AppointmentType type = AppointmentType.MARCACAO;

    @Column(name = "id_profissional", nullable = false)
    private String professionalId;

    @Column(name = "id_paciente", nullable = false)
    private String patientId;

    @Column(name = "observacao")
    private String observation;

    @CreatedDate
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime updatedAt;

}

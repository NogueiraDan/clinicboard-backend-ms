package com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidade JPA para persistência de dados de pacientes.
 * 
 * Esta é uma representação de infraestrutura que mapeia para o banco de dados,
 * separada do modelo de domínio para manter a independência da arquitetura hexagonal.
 */
@Entity
@Table(name = "patients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PatientJpaEntity {
    
    @Id
    @Column(name = "patient_id", length = 36)
    private String patientId;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "email", nullable = false, length = 150)
    private String email;
    
    @Column(name = "contact_details", length = 50)
    private String contactDetails;
    
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version")
    private Long version;
}

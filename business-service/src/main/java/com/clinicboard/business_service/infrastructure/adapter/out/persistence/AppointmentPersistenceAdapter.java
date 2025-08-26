package com.clinicboard.business_service.infrastructure.adapter.out.persistence;
import lombok.extern.slf4j.Slf4j;

import com.clinicboard.business_service.application.port.out.AppointmentRepository;
import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.model.AppointmentId;
import com.clinicboard.business_service.domain.model.PatientId;
import com.clinicboard.business_service.domain.model.ProfessionalId;
import com.clinicboard.business_service.domain.model.AppointmentTime;
import com.clinicboard.business_service.domain.model.AppointmentStatus;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.entity.AppointmentJpaEntity.AppointmentStatusEnum;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.repository.AppointmentJpaRepository;
import com.clinicboard.business_service.infrastructure.adapter.out.persistence.mapper.AppointmentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistência para Appointment.
 * 
 * Implementa a porta de saída AppointmentRepository usando JPA,
 * convertendo entre objetos de domínio e entidades de persistência.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentPersistenceAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentJpaRepository;
    private final AppointmentPersistenceMapper appointmentMapper;

    @Override
    public Appointment save(Appointment appointment) {
        log.debug("Salvando consulta: {}", appointment.getId());
        
        try {
            AppointmentJpaEntity jpaEntity;
            
            if (appointment.getId() == null) {
                // Nova consulta - gerar ID
                jpaEntity = appointmentMapper.toJpaEntity(appointment);
                jpaEntity.setAppointmentId(UUID.randomUUID().toString());
            } else {
                // Consulta existente - buscar e atualizar
                Optional<AppointmentJpaEntity> existingEntity = appointmentJpaRepository.findById(appointment.getId().value());
                
                if (existingEntity.isPresent()) {
                    jpaEntity = existingEntity.get();
                    appointmentMapper.updateJpaEntity(jpaEntity, appointment);
                } else {
                    // Consulta com ID mas não existe no banco
                    jpaEntity = appointmentMapper.toJpaEntity(appointment);
                }
            }
            
            AppointmentJpaEntity savedEntity = appointmentJpaRepository.save(jpaEntity);
            Appointment savedAppointment = appointmentMapper.toDomainEntity(savedEntity);
            
            log.info("Consulta salva com sucesso: id={}", savedEntity.getAppointmentId());
            return savedAppointment;
            
        } catch (Exception e) {
            log.error("Erro ao salvar consulta: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao persistir consulta", e);
        }
    }

    @Override
    public Optional<Appointment> findById(AppointmentId appointmentId) {
        log.debug("Buscando consulta por ID: {}", appointmentId.value());
        
        try {
            return appointmentJpaRepository.findActiveAppointmentById(appointmentId.value())
                    .map(appointmentMapper::toDomainEntity);
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consulta por ID {}: {}", appointmentId.value(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Appointment> findByPatientId(PatientId patientId) {
        log.debug("Buscando consultas por paciente: {}", patientId.value());
        
        try {
            return appointmentJpaRepository.findScheduledAppointmentsByPatient(patientId.value())
                    .stream()
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas por paciente {}: {}", patientId.value(), e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Appointment> findByProfessionalId(ProfessionalId professionalId) {
        log.debug("Buscando consultas por profissional: {}", professionalId.value());
        
        try {
            return appointmentJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getProfessionalId().equals(professionalId.value()))
                    .filter(entity -> entity.getStatus() != AppointmentStatusEnum.CANCELLED)
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas por profissional {}: {}", professionalId.value(), e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        log.debug("Buscando consultas por data: {}", date);
        
        try {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            return appointmentJpaRepository.findAppointmentsByPeriodAndStatus(
                    startOfDay, endOfDay, AppointmentStatusEnum.SCHEDULED)
                    .stream()
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas por data {}: {}", date, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Buscando consultas no período: {} - {}", startDate, endDate);
        
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            
            return appointmentJpaRepository.findAppointmentsByPeriodAndStatus(
                    startDateTime, endDateTime, AppointmentStatusEnum.SCHEDULED)
                    .stream()
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas por período: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Appointment> findByDateTimeRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.debug("Buscando consultas no período: {} - {}", startDateTime, endDateTime);
        
        try {
            return appointmentJpaRepository.findAppointmentsByPeriodAndStatus(
                    startDateTime, endDateTime, AppointmentStatusEnum.SCHEDULED)
                    .stream()
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas por período DateTime: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Appointment> findByStatus(AppointmentStatus status) {
        log.debug("Buscando consultas por status: {}", status);
        
        try {
            AppointmentStatusEnum jpaStatus = mapToJpaStatus(status);
            return appointmentJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getStatus() == jpaStatus)
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas por status {}: {}", status, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean hasConflictingAppointment(ProfessionalId professionalId, AppointmentTime appointmentTime) {
        log.debug("Verificando conflitos para profissional {} no horário {}", 
                professionalId.value(), appointmentTime.value());
        
        try {
            LocalDateTime requestedTime = appointmentTime.value();
            LocalDateTime startWindow = requestedTime.minusMinutes(30);
            LocalDateTime endWindow = requestedTime.plusMinutes(30);
            
            List<AppointmentJpaEntity> conflicts = appointmentJpaRepository.findConflictingAppointments(
                    professionalId.value(), requestedTime, startWindow, endWindow);
            
            boolean hasConflict = !conflicts.isEmpty();
            
            if (hasConflict) {
                log.warn("Conflito de horário detectado para profissional {} no horário {}. Consultas conflitantes: {}", 
                        professionalId.value(), requestedTime, conflicts.size());
            }
            
            return hasConflict;
            
        } catch (Exception e) {
            log.error("Erro ao verificar conflitos: {}", e.getMessage(), e);
            return false; // Em caso de erro, assume que não há conflito
        }
    }

    @Override
    public boolean hasConflictingAppointmentExcluding(ProfessionalId professionalId, 
                                                      AppointmentTime appointmentTime, 
                                                      AppointmentId excludeAppointmentId) {
        log.debug("Verificando conflitos para profissional {} no horário {} excluindo consulta {}", 
                professionalId.value(), appointmentTime.value(), excludeAppointmentId.value());
        
        try {
            LocalDateTime requestedTime = appointmentTime.value();
            LocalDateTime startWindow = requestedTime.minusMinutes(30);
            LocalDateTime endWindow = requestedTime.plusMinutes(30);
            
            List<AppointmentJpaEntity> conflicts = appointmentJpaRepository.findConflictingAppointments(
                    professionalId.value(), requestedTime, startWindow, endWindow);
            
            // Filtrar excluindo a consulta especificada
            boolean hasConflict = conflicts.stream()
                    .anyMatch(entity -> !entity.getAppointmentId().equals(excludeAppointmentId.value()));
            
            if (hasConflict) {
                log.warn("Conflito de horário detectado para profissional {} no horário {} (excluindo {})", 
                        professionalId.value(), requestedTime, excludeAppointmentId.value());
            }
            
            return hasConflict;
            
        } catch (Exception e) {
            log.error("Erro ao verificar conflitos excluindo consulta: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Appointment> findActiveAppointmentsByProfessionalAndDate(ProfessionalId professionalId, LocalDate date) {
        log.debug("Buscando consultas ativas para profissional {} na data {}", professionalId.value(), date);
        
        try {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            
            return appointmentJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getProfessionalId().equals(professionalId.value()))
                    .filter(entity -> entity.getStatus() != AppointmentStatusEnum.CANCELLED)
                    .filter(entity -> entity.getScheduledTime().isAfter(startOfDay.minusSeconds(1)))
                    .filter(entity -> entity.getScheduledTime().isBefore(endOfDay.plusSeconds(1)))
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar consultas ativas por profissional e data: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Appointment> findUpcomingAppointmentsByPatient(PatientId patientId, int limit) {
        log.debug("Buscando {} próximas consultas do paciente {}", limit, patientId.value());
        
        try {
            LocalDateTime now = LocalDateTime.now();
            
            return appointmentJpaRepository.findScheduledAppointmentsByPatient(patientId.value())
                    .stream()
                    .filter(entity -> entity.getScheduledTime().isAfter(now))
                    .limit(limit)
                    .map(appointmentMapper::toDomainEntity)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Erro ao buscar próximas consultas do paciente: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public boolean existsById(AppointmentId appointmentId) {
        log.debug("Verificando existência de consulta: {}", appointmentId.value());
        
        try {
            return appointmentJpaRepository.findActiveAppointmentById(appointmentId.value()).isPresent();
            
        } catch (Exception e) {
            log.error("Erro ao verificar existência de consulta {}: {}", appointmentId.value(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void deleteById(AppointmentId appointmentId) {
        log.debug("Cancelando consulta: {}", appointmentId.value());
        
        try {
            Optional<AppointmentJpaEntity> appointmentEntity = appointmentJpaRepository.findById(appointmentId.value());
            
            if (appointmentEntity.isPresent()) {
                AppointmentJpaEntity entity = appointmentEntity.get();
                entity.setStatus(AppointmentStatusEnum.CANCELLED);
                entity.setCancelledAt(LocalDateTime.now());
                appointmentJpaRepository.save(entity);
                
                log.info("Consulta cancelada com sucesso: id={}", appointmentId.value());
            } else {
                log.warn("Tentativa de cancelar consulta inexistente: {}", appointmentId.value());
            }
            
        } catch (Exception e) {
            log.error("Erro ao cancelar consulta {}: {}", appointmentId.value(), e.getMessage(), e);
            throw new RuntimeException("Falha ao cancelar consulta", e);
        }
    }

    @Override
    public long countByStatus(AppointmentStatus status) {
        log.debug("Contando consultas por status: {}", status);
        
        try {
            AppointmentStatusEnum jpaStatus = mapToJpaStatus(status);
            return appointmentJpaRepository.findAll()
                    .stream()
                    .filter(entity -> entity.getStatus() == jpaStatus)
                    .count();
                    
        } catch (Exception e) {
            log.error("Erro ao contar consultas por status {}: {}", status, e.getMessage(), e);
            return 0;
        }
    }

    // Método auxiliar para mapear status
    private AppointmentStatusEnum mapToJpaStatus(AppointmentStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING -> AppointmentStatusEnum.SCHEDULED;
            case CONFIRMED -> AppointmentStatusEnum.CONFIRMED;
            case SCHEDULED -> AppointmentStatusEnum.SCHEDULED;
            case IN_PROGRESS -> AppointmentStatusEnum.IN_PROGRESS;
            case COMPLETED -> AppointmentStatusEnum.COMPLETED;
            case CANCELLED -> AppointmentStatusEnum.CANCELLED;
            case NO_SHOW -> AppointmentStatusEnum.NO_SHOW;
            case RESCHEDULED -> AppointmentStatusEnum.SCHEDULED;
        };
    }
}

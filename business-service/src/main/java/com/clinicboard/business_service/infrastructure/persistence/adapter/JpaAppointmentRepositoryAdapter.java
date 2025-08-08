package com.clinicboard.business_service.infrastructure.persistence.adapter;

import com.clinicboard.business_service.domain.model.Appointment;
import com.clinicboard.business_service.domain.port.AppointmentRepositoryPort;
import com.clinicboard.business_service.infrastructure.persistence.entity.AppointmentEntity;
import com.clinicboard.business_service.infrastructure.persistence.mapper.AppointmentEntityMapper;
import com.clinicboard.business_service.infrastructure.persistence.repository.SpringAppointmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa a Port de repositório usando infraestrutura JPA.
 * Esta classe faz a ponte entre o domínio puro e a tecnologia de persistência.
 */
@Repository
public class JpaAppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final SpringAppointmentRepository springRepository;
    private final AppointmentEntityMapper mapper;

    @Autowired
    public JpaAppointmentRepositoryAdapter(SpringAppointmentRepository springRepository, 
                                         AppointmentEntityMapper mapper) {
        this.springRepository = springRepository;
        this.mapper = mapper;
    }

    @Override
    public Appointment save(Appointment appointment) {
        AppointmentEntity entity = mapper.toEntity(appointment);
        AppointmentEntity savedEntity = springRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return springRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Appointment> findAll() {
        return springRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        springRepository.deleteById(id);
    }

    @Override
    public boolean existsByProfessionalIdAndDateRange(String professionalId, LocalDateTime startRange, LocalDateTime endRange) {
        return springRepository.existsByProfessionalIdAndDate(professionalId, startRange, endRange);
    }

    @Override
    public boolean existsByDateRange(LocalDateTime startRange, LocalDateTime endRange) {
        return springRepository.existsByDate(startRange, endRange);
    }

    @Override
    public boolean existsByPatientIdAndDate(LocalDateTime date, String patientId) {
        return springRepository.existsByPatientIdAndDate(date, patientId);
    }

    @Override
    public List<Appointment> findByStatusAndProfessionalId(String status, String professionalId) {
        return springRepository.findByStatus(professionalId, status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByProfessionalId(String professionalId) {
        return springRepository.findByProfessionalId(professionalId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> findByPatientId(String patientId) {
        return springRepository.findByPatientId(patientId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

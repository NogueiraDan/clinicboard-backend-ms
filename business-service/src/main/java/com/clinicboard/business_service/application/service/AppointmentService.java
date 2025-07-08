package com.clinicboard.business_service.application.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.clinicboard.business_service.api.contract.AppointmentServiceInterface;
import com.clinicboard.business_service.api.contract.MessagingInterface;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;
import com.clinicboard.business_service.application.dto.AppointmentResponseDto;
import com.clinicboard.business_service.application.mapper.AppointmentMapper;
import com.clinicboard.business_service.common.error.BusinessException;
import com.clinicboard.business_service.common.error.CustomGenericException;
import com.clinicboard.business_service.domain.entity.Appointment;
import com.clinicboard.business_service.domain.repository.AppointmentRepository;

@Service
public class AppointmentService implements AppointmentServiceInterface {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final MessagingInterface messagingInterface;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper,
            MessagingInterface messagingInterface) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.messagingInterface = messagingInterface;
    }

    @Override
    public AppointmentResponseDto save(AppointmentRequestDto appointment) {
        validateAppointment(appointment);
        var savedAppointment = appointmentRepository.save(appointmentMapper.toEntity(appointment));
        messagingInterface.publishNotification(appointment);
        return appointmentMapper.toDto(savedAppointment);
    }

    @Override
    public List<AppointmentResponseDto> findAll() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public AppointmentResponseDto findById(String id) {
        return appointmentMapper.toDto(appointmentRepository.findById(id)
                .orElseThrow(() -> new CustomGenericException("Consulta não encontrada")));
    }

    @Override
    public List<AppointmentResponseDto> findByProfessionalId(String id) {
        return appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getProfessionalId().equals(id))
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByPatientId(String id) {
        return appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getPatientId().equals(id))
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByFilter(String id, String param, String value) {
        List<Appointment> appointments;
        value = "%" + value + "%";
        switch (param.toLowerCase()) {
            case "status":
                appointments = appointmentRepository.findByStatus(id, value);
                break;
            default:
                throw new BusinessException("Parâmetro de busca inválido.");
        }
        return appointments.stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    @Override
    public AppointmentResponseDto update(String id, AppointmentRequestDto appointment) {
        var existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new CustomGenericException("Consulta não encontrada para atualização."));

        appointmentMapper.updateEntity(appointment, existingAppointment);
        validateAppointment(appointment);
        return appointmentMapper.toDto(appointmentRepository.save(existingAppointment));
    }

    @Override
    public void delete(String id) {
        if (!appointmentRepository.existsById(id)) {
            throw new CustomGenericException("Consulta não encontrada para exclusão.");
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    public AppointmentRepository getAppointmentRepository() {
        return this.appointmentRepository;
    }

    @Override
    public List<AppointmentResponseDto> findByDate(String id, String date) {
        return appointmentRepository.findByDate(id, date).stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    private void validateAppointment(AppointmentRequestDto appointment) {
        LocalDateTime appointmentDate = appointment.getDate();
        LocalDateTime startRange = appointmentDate.minusMinutes(30);
        LocalDateTime endRange = appointmentDate.plusMinutes(30);

        LocalTime appointmentTime = appointmentDate.toLocalTime();
        LocalTime startBusinessHour = LocalTime.of(8, 0);
        LocalTime endBusinessHour = LocalTime.of(19, 0);

        // Verifica se o horário do agendamento está dentro do horário comercial
        if (appointmentTime.isBefore(startBusinessHour) || appointmentTime.isAfter(endBusinessHour)) {
            throw new BusinessException("O horário do agendamento está fora do horário comercial (08:00 às 19:00).");
        }

        // Data ocupada por profissional
        boolean isProfessionalBusy = appointmentRepository.existsByProfessionalIdAndDate(
                appointment.getProfessionalId(),
                startRange,
                endRange);
        if (isProfessionalBusy) {
            throw new BusinessException(
                    "Profissional já possui um agendamento nesta data/hora ou horário está fora do intervalo permitido.");
        }

        // Data ocupada por quaisquer outro profissional
        boolean isDateBusy = appointmentRepository.existsByDate(startRange, endRange);
        if (isDateBusy) {
            throw new BusinessException(
                    "Já existe um agendamento marcado nesta data/hora ou horário está fora do intervalo permitido.");
        }

        // Paciente já tem agendamento marcado para aquele dia
        boolean isPatientBusy = appointmentRepository.existsByPatientIdAndDate(appointmentDate,
                appointment.getPatientId());
        if (isPatientBusy) {
            throw new BusinessException("Paciente já possui um agendamento nesta data!");
        }
    }

    @Override
    public List<LocalTime> getAvailableTimes(String id, String date) {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(21, 0);

        // Busca todos os agendamentos para a data
        List<Appointment> appointments = appointmentRepository.findByDate(id, date);

        // Coleta horários ocupados
        Set<LocalTime> busyTimes = new HashSet<>();
        for (Appointment appointment : appointments) {
            busyTimes.add(appointment.getDate().toLocalTime().withSecond(0).withNano(0));
        }

        // Gera lista de horários livres
        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime current = start;
        while (!current.isAfter(end)) {
            if (!busyTimes.contains(current)) {
                availableTimes.add(current);
            }
            current = current.plusMinutes(30);
        }
        return availableTimes;
    }

}

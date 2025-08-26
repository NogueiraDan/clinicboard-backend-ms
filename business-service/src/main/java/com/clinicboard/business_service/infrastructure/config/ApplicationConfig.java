package com.clinicboard.business_service.infrastructure.config;

import com.clinicboard.business_service.application.port.in.*;
import com.clinicboard.business_service.application.port.out.AppointmentRepository;
import com.clinicboard.business_service.application.port.out.EventPublisher;
import com.clinicboard.business_service.application.port.out.PatientRepository;
import com.clinicboard.business_service.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da camada de aplicação.
 * 
 * Define os beans dos casos de uso, conectando as portas
 * de entrada com as implementações dos adapters.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Bean do caso de uso de gerenciar pacientes
     */
    @Bean
    public ManagePatientCommand managePatientCommand(PatientRepository patientRepository) {
        return new ManagePatientUseCaseImpl(patientRepository);
    }

    /**
     * Bean do caso de uso de buscar pacientes
     */
    @Bean
    public FindPatientQuery findPatientQuery(PatientRepository patientRepository) {
        return new FindPatientUseCaseImpl(patientRepository);
    }

    /**
     * Bean do caso de uso de agendar consultas
     */
    @Bean
    public ScheduleAppointmentCommand scheduleAppointmentCommand(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            EventPublisher eventPublisher) {
        return new ScheduleAppointmentUseCaseImpl(appointmentRepository, patientRepository, eventPublisher);
    }

    /**
     * Bean do caso de uso de cancelar consultas
     */
    @Bean
    public CancelAppointmentCommand cancelAppointmentCommand(
            AppointmentRepository appointmentRepository,
            EventPublisher eventPublisher) {
        return new CancelAppointmentUseCaseImpl(appointmentRepository, eventPublisher);
    }

    /**
     * Bean do caso de uso de buscar consultas
     */
    @Bean
    public FindAppointmentQuery findAppointmentQuery(AppointmentRepository appointmentRepository) {
        return new FindAppointmentUseCaseImpl(appointmentRepository);
    }
}

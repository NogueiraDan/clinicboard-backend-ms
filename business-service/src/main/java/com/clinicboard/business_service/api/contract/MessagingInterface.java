package com.clinicboard.business_service.api.contract;
import com.clinicboard.business_service.application.dto.AppointmentRequestDto;

public interface MessagingInterface {
    void publishNotification(AppointmentRequestDto appointment);
}

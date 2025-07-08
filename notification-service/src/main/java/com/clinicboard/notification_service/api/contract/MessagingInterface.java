package com.clinicboard.notification_service.api.contract;

import com.clinicboard.notification_service.api.dto.AppointmentRequestDto;

public interface MessagingInterface {
    void receiveMessageFromDLQ(AppointmentRequestDto appointment);
    void receiveMessage(AppointmentRequestDto appointment);
}

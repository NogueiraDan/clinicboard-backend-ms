package com.clinicboard.notification_service.config;

import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("com.clinicboard.business_service.application.dto.AppointmentRequestDto",
                com.clinicboard.notification_service.api.dto.AppointmentRequestDto.class);
        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);
        return converter;
    }
}

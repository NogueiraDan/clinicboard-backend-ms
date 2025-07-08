package com.clinicboard.business_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@EnableRabbit
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class BusinessServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusinessServiceApplication.class, args);
	}

}

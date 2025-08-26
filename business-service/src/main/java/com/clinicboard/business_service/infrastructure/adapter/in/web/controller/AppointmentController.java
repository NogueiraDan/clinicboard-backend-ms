package com.clinicboard.business_service.infrastructure.adapter.in.web.controller;

import com.clinicboard.business_service.application.port.in.ScheduleAppointmentCommand;
import com.clinicboard.business_service.application.port.in.CancelAppointmentCommand;
import com.clinicboard.business_service.application.port.in.FindAppointmentQuery;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.ScheduleAppointmentRequestDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.AppointmentResponseDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.mapper.AppointmentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciamento de consultas.
 * 
 * Adaptador de entrada que converte requisições HTTP em comandos/queries
 * da camada de aplicação, mantendo o isolamento do domínio.
 * 
 * Princípios aplicados:
 * - Arquitetura Hexagonal: adaptador para porta de entrada
 * - CQRS: separação entre comandos e queries
 * - Responsabilidade única: apenas conversão HTTP ↔ Application
 */
@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Operações de gerenciamento de consultas")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final ScheduleAppointmentCommand scheduleAppointmentCommand;
    private final CancelAppointmentCommand cancelAppointmentCommand;
    private final FindAppointmentQuery findAppointmentQuery;
    private final AppointmentWebMapper appointmentMapper;

    @Operation(
        summary = "Agendar nova consulta",
        description = "Cria um novo agendamento de consulta no sistema"
    )
    @ApiResponse(responseCode = "201", description = "Consulta agendada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Conflito de horário")
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> scheduleAppointment(
            @Valid @RequestBody ScheduleAppointmentRequestDto request) {
        
        log.info("Recebida requisição para agendar consulta: patientId={}, scheduledTime={}", 
                request.patientId(), request.scheduledTime());
        
        try {
            var applicationRequest = appointmentMapper.toApplicationRequest(request);
            var response = scheduleAppointmentCommand.scheduleAppointment(applicationRequest);
            var responseDto = appointmentMapper.toResponseDto(response);
            
            log.info("Consulta agendada com sucesso: appointmentId={}", responseDto.appointmentId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (Exception e) {
            log.error("Erro ao agendar consulta: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler tratará as exceptions
        }
    }

    @Operation(
        summary = "Buscar consulta",
        description = "Busca uma consulta específica por ID"
    )
    @ApiResponse(responseCode = "200", description = "Consulta encontrada")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> getAppointment(
            @Parameter(description = "ID da consulta") 
            @PathVariable String appointmentId) {
        
        log.info("Buscando consulta: appointmentId={}", appointmentId);
        
        // TODO: Implementar quando FindAppointmentQuery estiver com records corretos
        // var response = findAppointmentQuery.findAppointment(appointmentId);
        // var responseDto = appointmentMapper.toResponseDto(response);
        
        // Placeholder temporário
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Cancelar consulta",
        description = "Cancela uma consulta agendada"
    )
    @ApiResponse(responseCode = "200", description = "Consulta cancelada com sucesso")
    @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    @ApiResponse(responseCode = "400", description = "Consulta não pode ser cancelada")
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @Parameter(description = "ID da consulta") 
            @PathVariable String appointmentId,
            @Parameter(description = "Motivo do cancelamento") 
            @RequestParam(required = false) String reason) {
        
        log.info("Cancelando consulta: appointmentId={}, reason={}", appointmentId, reason);
        
        // TODO: Implementar quando CancelAppointmentCommand estiver com records corretos
        // var response = cancelAppointmentCommand.cancelAppointment(appointmentId, reason);
        // var responseDto = appointmentMapper.toResponseDto(response);
        
        // Placeholder temporário
        return ResponseEntity.ok().build();
    }
}

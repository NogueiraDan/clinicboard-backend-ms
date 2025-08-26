package com.clinicboard.business_service.infrastructure.adapter.in.web.controller;

import com.clinicboard.business_service.application.port.in.ManagePatientCommand;
import com.clinicboard.business_service.application.port.in.FindPatientQuery;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.ManagePatientRequestDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.dto.PatientResponseDto;
import com.clinicboard.business_service.infrastructure.adapter.in.web.mapper.PatientWebMapper;
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
 * Controller REST para gerenciamento de pacientes.
 * 
 * Adaptador de entrada que converte requisições HTTP em comandos/queries
 * da camada de aplicação para operações relacionadas a pacientes.
 */
@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Operações de gerenciamento de pacientes")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final ManagePatientCommand managePatientCommand;
    private final FindPatientQuery findPatientQuery;
    private final PatientWebMapper patientMapper;

    @Operation(
        summary = "Registrar novo paciente",
        description = "Cria um novo paciente no sistema"
    )
    @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "Paciente já existe")
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(
            @Valid @RequestBody ManagePatientRequestDto request) {
        
        log.info("Recebida requisição para criar paciente: name={}, email={}", 
                request.name(), request.email());
        
        try {
            var applicationRequest = patientMapper.toApplicationRequest(request);
            var response = managePatientCommand.createPatient(applicationRequest);
            var responseDto = patientMapper.toResponseDto(response);
            
            log.info("Paciente criado com sucesso: patientId={}", responseDto.patientId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
            
        } catch (Exception e) {
            log.error("Erro ao criar paciente: {}", e.getMessage(), e);
            throw e; // GlobalExceptionHandler tratará as exceptions
        }
    }

    @Operation(
        summary = "Buscar paciente",
        description = "Busca um paciente específico por ID"
    )
    @ApiResponse(responseCode = "200", description = "Paciente encontrado")
    @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponseDto> getPatient(
            @Parameter(description = "ID do paciente") 
            @PathVariable String patientId) {
        
        log.info("Buscando paciente: patientId={}", patientId);
        
        // TODO: Implementar quando FindPatientQuery estiver com records corretos
        // var response = findPatientQuery.findPatient(patientId);
        // var responseDto = patientMapper.toResponseDto(response);
        
        // Placeholder temporário
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "Atualizar paciente",
        description = "Atualiza dados de um paciente existente"
    )
    @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @Parameter(description = "ID do paciente") 
            @PathVariable String patientId,
            @Valid @RequestBody ManagePatientRequestDto request) {
        
        log.info("Atualizando paciente: patientId={}", patientId);
        
        // TODO: Implementar quando UpdatePatientRequest estiver disponível
        // var response = managePatientCommand.updatePatient(request);
        // var responseDto = patientMapper.toResponseDto(response);
        
        // Placeholder temporário
        return ResponseEntity.ok().build();
    }
}

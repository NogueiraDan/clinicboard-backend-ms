package com.clinicboard.user_service.domain.service;

import com.clinicboard.user_service.domain.exception.BusinessException;
import com.clinicboard.user_service.domain.model.UserRole;

/**
 * Domain Service responsável por implementar regras complexas de política de senha.
 * 
 * Este é um exemplo de Domain Service porque:
 * 1. Implementa lógica de negócio que não pertence naturalmente a uma entidade específica
 * 2. Coordena regras que podem envolver múltiplos conceitos do domínio
 * 3. Encapsula conhecimento sobre políticas de segurança do negócio
 */
public class PasswordPolicyDomainService {
    
    /**
     * Valida se uma senha atende aos critérios de política baseados no papel do usuário.
     * 
     * @param rawPassword A senha em texto plano a ser validada
     * @param userRole O papel do usuário (influencia a complexidade requerida)
     * @throws BusinessException se a senha não atender aos critérios
     */
    public void validatePasswordPolicy(String rawPassword, UserRole userRole) {
        // Regras básicas para todos os usuários
        validateBasicPasswordRules(rawPassword);
        
        // Regras específicas baseadas no papel
        if (userRole.isAdmin()) {
            validateAdministratorPasswordRules(rawPassword);
        } else if (userRole.isProfessional()) {
            validateMedicalStaffPasswordRules(rawPassword);
        }
    }
    
    /**
     * Determina se é necessário forçar a troca de senha baseado em regras de negócio.
     * 
     * @param userRole O papel do usuário
     * @param daysSinceLastChange Dias desde a última troca de senha
     * @return true se a senha deve ser alterada obrigatoriamente
     */
    public boolean shouldForcePasswordChange(UserRole userRole, int daysSinceLastChange) {
        // Administradores devem trocar senha a cada 60 dias
        if (userRole.isAdmin() && daysSinceLastChange >= 60) {
            return true;
        }
        
        // Staff médico deve trocar senha a cada 90 dias
        if (userRole.isProfessional() && daysSinceLastChange >= 90) {
            return true;
        }
        
        // Usuários comuns devem trocar senha a cada 120 dias
        return daysSinceLastChange >= 120;
    }
    
    private void validateBasicPasswordRules(String password) {
        if (password.length() < 8) {
            throw new BusinessException("Senha deve ter pelo menos 8 caracteres");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("Senha deve conter pelo menos uma letra maiúscula");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException("Senha deve conter pelo menos uma letra minúscula");
        }
        
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException("Senha deve conter pelo menos um número");
        }
    }
    
    private void validateAdministratorPasswordRules(String password) {
        if (password.length() < 12) {
            throw new BusinessException("Senha de administrador deve ter pelo menos 12 caracteres");
        }
        
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new BusinessException("Senha de administrador deve conter pelo menos um caractere especial");
        }
    }
    
    private void validateMedicalStaffPasswordRules(String password) {
        if (password.length() < 10) {
            throw new BusinessException("Senha de profissional médico deve ter pelo menos 10 caracteres");
        }
        
        // Não pode conter sequências comuns
        if (password.toLowerCase().contains("123") || 
            password.toLowerCase().contains("abc") ||
            password.toLowerCase().contains("hospital") ||
            password.toLowerCase().contains("clinica")) {
            throw new BusinessException("Senha não pode conter sequências comuns ou termos médicos");
        }
    }
}

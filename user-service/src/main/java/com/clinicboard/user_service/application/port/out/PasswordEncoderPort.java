package com.clinicboard.user_service.application.port.out;

/**
 * Porta de saída para codificação de senhas.
 * Permite que a camada de aplicação permaneça agnóstica à implementação específica
 * de criptografia, seguindo o princípio de inversão de dependência.
 */
public interface PasswordEncoderPort {
    
    /**
     * Codifica uma senha em texto puro.
     * 
     * @param rawPassword A senha em texto puro a ser codificada
     * @return A senha codificada/hash
     */
    String encode(String rawPassword);
}

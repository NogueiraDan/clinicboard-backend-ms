package com.clinicboard.user_service.infrastructure.adapter.out;

import com.clinicboard.user_service.application.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa a porta de codificação de senhas
 * usando BCrypt como estratégia de hash.
 */
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    public PasswordEncoderAdapter() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
}

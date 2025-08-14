package com.clinicboard.user_service.infrastructure.adapter.inbound.web.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.clinicboard.user_service.application.port.outbound.TokenServicePort;
import com.clinicboard.user_service.domain.model.User;

import org.springframework.beans.factory.annotation.Value;

@Service
public class TokenService implements TokenServicePort {

    @Value("${api.security.token.secret}")
    private String secret;
    
    @Value("${api.security.token.expiration:7200}") // 2 horas por padrão
    private Long expirationTime;

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("clinicboard-api")
                    .withSubject(user.getIdValue())
                    .withClaim("email", user.getEmailValue())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("clinicboard-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }
    
    @Override
    public Long getExpirationTime() {
        return expirationTime;
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusSeconds(expirationTime).toInstant(ZoneOffset.of("-03:00"));
    }

}

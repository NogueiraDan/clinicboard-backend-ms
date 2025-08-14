package com.clinicboard.user_service.infrastructure.adapter.inbound.web.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clinicboard.user_service.application.port.inbound.UserUseCase;
import com.clinicboard.user_service.application.port.outbound.TokenServicePort;
import com.clinicboard.user_service.application.dto.UserResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final UserUseCase userUseCase;
    private final TokenServicePort tokenService;

    public SecurityFilter(UserUseCase userUseCase, TokenServicePort tokenService) {
        this.userUseCase = userUseCase;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recoverToken(request);
        if (token != null) {
            String userId = tokenService.validateToken(token);
            if (!userId.isEmpty()) {
                try {
                    UserResponseDto userResponse = userUseCase.findUserById(userId);
                    if (userResponse != null) {
                        // Create a simple UserDetails implementation for Spring Security
                        UserDetails user = new org.springframework.security.core.userdetails.User(
                            userResponse.getEmail(),
                            "", // password não precisa ser verificado aqui
                            java.util.Collections.emptyList() // authorities podem ser implementadas depois
                        );
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    // Log error and continue without authentication
                    // O usuário ficará não autenticado
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }

}

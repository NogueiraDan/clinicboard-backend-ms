package com.clinicboard.user_service.infrastructure.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clinicboard.user_service.application.port.out.UserRepositoryPort;

import com.clinicboard.user_service.infrastructure.adapter.out.authentication.UserDetailsAdapter;
import com.clinicboard.user_service.domain.model.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de segurança para interceptação e validação de tokens JWT.
 * Movido para infrastructure pois é detalhe de implementação.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenService tokenService;

    public SecurityFilter(UserRepositoryPort userRepositoryPort, TokenService tokenService) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var token = this.recoverToken(request);
        if (token != null) {
            var userId = tokenService.validateToken(token);
            if (!userId.isEmpty()) {
                var userOptional = userRepositoryPort.findById(new UserId(userId));
                if (userOptional.isPresent()) {
                    UserDetails user = new UserDetailsAdapter(userOptional.get());
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
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

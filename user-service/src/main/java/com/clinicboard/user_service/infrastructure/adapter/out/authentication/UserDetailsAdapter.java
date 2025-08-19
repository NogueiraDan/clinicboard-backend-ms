package com.clinicboard.user_service.infrastructure.adapter.out.authentication;

import com.clinicboard.user_service.domain.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter que converte a entidade User do domínio para UserDetails do Spring Security.
 * Mantém o domínio puro, isolando a dependência do framework na camada de infraestrutura.
 */
public class UserDetailsAdapter implements UserDetails {
    
    private final User user;
    
    public UserDetailsAdapter(User user) {
        this.user = user;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole().isAdmin()) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESSIONAL"));
        }
    }
    
    @Override
    public String getPassword() {
        return user.getDomainPassword() != null ? user.getDomainPassword().getValue() : null;
    }
    
    @Override
    public String getUsername() {
        return user.getEmail() != null ? user.getEmail().getValue() : null;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true; // Pode adicionar lógica baseada no domínio se necessário
    }
    
    /**
     * Método para acessar o domínio puro se necessário
     */
    public User getDomainUser() {
        return user;
    }
}
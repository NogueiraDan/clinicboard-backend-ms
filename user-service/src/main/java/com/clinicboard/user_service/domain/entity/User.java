package com.clinicboard.user_service.domain.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.clinicboard.user_service.api.dto.UserRole;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "usuarios")
@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private String id;

    @Column(name = "nome", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "senha", nullable = false)
    private String password;

    @Column(name = "contato", nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private UserRole role = UserRole.PROFESSIONAL;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_PROFESSIONAL"));
    }

    @Override
    public String getUsername() {
        return email;
    }

}

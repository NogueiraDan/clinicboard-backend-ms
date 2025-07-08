package com.clinicboard.user_service.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.clinicboard.user_service.domain.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByEmail(String email);
}

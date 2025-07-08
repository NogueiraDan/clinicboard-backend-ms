package com.clinicboard.user_service.application.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clinicboard.user_service.api.contract.UserServiceInterface;
import com.clinicboard.user_service.domain.repository.UserRepository;

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserServiceInterface userServiceInterface;

    public AuthorizationService(UserServiceInterface userServiceInterface) {
        this.userServiceInterface = userServiceInterface;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRepository userRepository = userServiceInterface.getUserRepository();
        UserDetails user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return user;
    }

}

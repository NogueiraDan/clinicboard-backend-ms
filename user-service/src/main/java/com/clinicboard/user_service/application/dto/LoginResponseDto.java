package com.clinicboard.user_service.application.dto;

/**
 * DTO de resposta para requisições de login - contém token e dados do usuário
 */
public class LoginResponseDto {
    
    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserResponseDto user;
    
    public LoginResponseDto() {}
    
    public LoginResponseDto(String token, String tokenType, Long expiresIn, UserResponseDto user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserResponseDto getUser() {
        return user;
    }
    
    public void setUser(UserResponseDto user) {
        this.user = user;
    }
}

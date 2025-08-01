package com.devtective.devtective.dominio.auth;

public class LoginResponseDTO {
    private String token;

    private String username;
    private Long userId;
    public LoginResponseDTO(String token, String username, Long userId) {
        this.token = token;
        this.username = username;
        this.userId = userId;
    }

    public String getToken() {
        return this.token;
    }
    public String getUsername() {
        return this.username;
    }
    public Long getUserId() {
        return this.userId;
    }
}

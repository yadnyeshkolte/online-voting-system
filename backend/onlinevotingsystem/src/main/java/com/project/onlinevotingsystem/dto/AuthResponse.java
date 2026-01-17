package com.project.onlinevotingsystem.dto;

public class AuthResponse {
    private String jwt;
    private String role;
    private Long id;

    public AuthResponse() {
    }

    public AuthResponse(String jwt, String role, Long id) {
        this.jwt = jwt;
        this.role = role;
        this.id = id;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return id;
    }

    public void setUserId(Long id) {
        this.id = id;
    }
}

package com.syfe.personalfinancemanager.dto.auth;

public class RegisterResponse {

    private String message;
    private Long userId;

    public RegisterResponse(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }
}
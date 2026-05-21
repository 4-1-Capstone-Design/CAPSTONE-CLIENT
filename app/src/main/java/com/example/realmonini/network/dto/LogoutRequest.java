package com.example.realmonini.network.dto;

public class LogoutRequest {
    private String refreshToken;

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

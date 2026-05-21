package com.example.realmonini.network.dto;

public class LoginData {
    private long userId;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    public long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
}

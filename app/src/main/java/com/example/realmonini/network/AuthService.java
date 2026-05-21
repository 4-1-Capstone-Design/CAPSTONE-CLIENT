package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.LoginData;
import com.example.realmonini.network.dto.LoginRequest;
import com.example.realmonini.network.dto.LogoutRequest;
import com.example.realmonini.network.dto.SignupData;
import com.example.realmonini.network.dto.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/api/v1/auth/login")
    Call<ApiResponse<LoginData>> login(@Body LoginRequest request);

    @POST("/api/v1/auth/signup")
    Call<ApiResponse<SignupData>> signup(@Body SignupRequest request);

    @POST("/api/v1/auth/logout")
    Call<ApiResponse<Object>> logout(@Body LogoutRequest request);
}

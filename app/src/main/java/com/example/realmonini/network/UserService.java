package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserService {
    @GET("/api/v1/users/me")
    Call<ApiResponse<UserData>> getMe(@Header("Authorization") String authorization);
}

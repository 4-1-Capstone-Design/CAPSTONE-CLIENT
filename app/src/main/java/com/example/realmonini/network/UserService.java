package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.UserData;
import com.example.realmonini.network.dto.WithdrawRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;

public interface UserService {
    @GET("/api/v1/users/me")
    Call<ApiResponse<UserData>> getMe(@Header("Authorization") String authorization);

    @HTTP(method = "DELETE", path = "/api/v1/users/me", hasBody = true)
    Call<ApiResponse<Object>> withdraw(
            @Header("Authorization") String authorization,
            @Body WithdrawRequest body
    );
}

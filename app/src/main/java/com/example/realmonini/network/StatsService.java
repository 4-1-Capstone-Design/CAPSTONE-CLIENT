package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.MonthlyStatsData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface StatsService {
    @GET("/api/v1/stats/monthly")
    Call<ApiResponse<MonthlyStatsData>> getMonthly(@Header("Authorization") String authorization,
                                                   @Query("year") int year,
                                                   @Query("month") int month);
}

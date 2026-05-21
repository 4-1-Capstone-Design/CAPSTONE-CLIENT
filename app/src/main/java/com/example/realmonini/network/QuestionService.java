package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.SubmitData;
import com.example.realmonini.network.dto.SubmitRequest;
import com.example.realmonini.network.dto.TodayQuestionsData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface QuestionService {
    @GET("/api/v1/journals/questions/today")
    Call<ApiResponse<TodayQuestionsData>> getToday(@Header("Authorization") String authorization);

    @POST("/api/v1/journals/questions/submit")
    Call<ApiResponse<SubmitData>> submit(@Header("Authorization") String authorization,
                                         @Body SubmitRequest request);
}

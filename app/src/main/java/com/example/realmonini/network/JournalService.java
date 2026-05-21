package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.ReplyData;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface JournalService {
    @POST("/api/v1/journals/{journalId}/reply")
    Call<ApiResponse<ReplyData>> getReply(@Header("Authorization") String authorization,
                                          @Path("journalId") long journalId);
}

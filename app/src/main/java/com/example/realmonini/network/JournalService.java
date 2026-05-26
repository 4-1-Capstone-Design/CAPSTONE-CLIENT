package com.example.realmonini.network;

import com.example.realmonini.network.dto.ApiResponse;
import com.example.realmonini.network.dto.JournalDetailData;
import com.example.realmonini.network.dto.JournalListData;
import com.example.realmonini.network.dto.JournalReplyData;
import com.example.realmonini.network.dto.KeywordItem;
import com.example.realmonini.network.dto.ReplyData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JournalService {
    @GET("/api/v1/journals")
    Call<ApiResponse<JournalListData>> getJournals(@Header("Authorization") String authorization,
                                                   @Query("cursor") Long cursor,
                                                   @Query("size") int size);

    @GET("/api/v1/journals/by-date")
    Call<ApiResponse<JournalDetailData>> getByDate(@Header("Authorization") String authorization,
                                                   @Query("year") int year,
                                                   @Query("month") int month,
                                                   @Query("day") int day);

    @GET("/api/v1/journals/{journalId}/reply")
    Call<ApiResponse<JournalReplyData>> getStoredReply(@Header("Authorization") String authorization,
                                                       @Path("journalId") long journalId);

    @GET("/api/v1/journals/{journalId}/keywords")
    Call<ApiResponse<List<KeywordItem>>> getKeywords(@Header("Authorization") String authorization,
                                                     @Path("journalId") long journalId);

    @POST("/api/v1/journals/{journalId}/reply")
    Call<ApiResponse<ReplyData>> generateReply(@Header("Authorization") String authorization,
                                               @Path("journalId") long journalId);
}

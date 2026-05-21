package com.example.realmonini.network.dto;

public class AnswerRequest {
    private long dailyQuestionId;
    private String content;

    public AnswerRequest(long dailyQuestionId, String content) {
        this.dailyQuestionId = dailyQuestionId;
        this.content = content;
    }
}

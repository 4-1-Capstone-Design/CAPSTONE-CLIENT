package com.example.realmonini.network.dto;

public class QuestionItem {
    private long dailyQuestionId;
    private String content;
    private String category;
    private int displayOrder;

    public long getDailyQuestionId() { return dailyQuestionId; }
    public String getContent() { return content; }
    public int getDisplayOrder() { return displayOrder; }
}

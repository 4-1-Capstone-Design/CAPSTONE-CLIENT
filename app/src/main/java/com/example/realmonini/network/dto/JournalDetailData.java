package com.example.realmonini.network.dto;

import java.util.List;

public class JournalDetailData {
    private long journalId;
    private String title;
    private String content;
    private String journalDate;
    private String createdAt;
    private List<QuestionAnswerItem> questions;

    public long getJournalId() { return journalId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getJournalDate() { return journalDate; }
    public String getCreatedAt() { return createdAt; }
    public List<QuestionAnswerItem> getQuestions() { return questions; }
}

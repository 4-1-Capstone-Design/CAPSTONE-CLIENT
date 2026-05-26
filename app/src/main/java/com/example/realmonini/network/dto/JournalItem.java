package com.example.realmonini.network.dto;

public class JournalItem {
    private long journalId;
    private String title;
    private String content;
    private String journalDate;
    private String createdAt;

    public long getJournalId() { return journalId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getJournalDate() { return journalDate; }
    public String getCreatedAt() { return createdAt; }
}

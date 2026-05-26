package com.example.realmonini.network.dto;

public class JournalReplyData {
    private long replyId;
    private long journalId;
    private String content;
    private String modelName;
    private String createdAt;

    public long getReplyId() { return replyId; }
    public long getJournalId() { return journalId; }
    public String getContent() { return content; }
    public String getModelName() { return modelName; }
    public String getCreatedAt() { return createdAt; }
}

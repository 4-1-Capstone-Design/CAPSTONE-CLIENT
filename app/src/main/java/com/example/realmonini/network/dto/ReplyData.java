package com.example.realmonini.network.dto;

import java.util.List;

public class ReplyData {
    private long journalId;
    private String summary;
    private String reply;
    private List<KeywordItem> keywords;

    public long getJournalId() { return journalId; }
    public String getSummary() { return summary; }
    public String getReply() { return reply; }
    public List<KeywordItem> getKeywords() { return keywords; }
}

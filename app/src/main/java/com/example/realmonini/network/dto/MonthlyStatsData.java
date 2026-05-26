package com.example.realmonini.network.dto;

import java.util.List;

public class MonthlyStatsData {
    private int year;
    private int month;
    private long journalCount;
    private long answerCount;
    private String topEmotion;
    private List<EmotionDistribution> emotionDistribution;

    public int getYear() { return year; }
    public int getMonth() { return month; }
    public long getJournalCount() { return journalCount; }
    public long getAnswerCount() { return answerCount; }
    public String getTopEmotion() { return topEmotion; }
    public List<EmotionDistribution> getEmotionDistribution() { return emotionDistribution; }
}

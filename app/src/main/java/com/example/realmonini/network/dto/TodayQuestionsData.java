package com.example.realmonini.network.dto;

import java.util.List;

public class TodayQuestionsData {
    private String questionDate;
    private List<QuestionItem> questions;

    public String getQuestionDate() { return questionDate; }
    public List<QuestionItem> getQuestions() { return questions; }
}

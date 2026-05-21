package com.example.realmonini.network.dto;

import java.util.List;

public class SubmitRequest {
    private String title;
    private List<AnswerRequest> answers;

    public SubmitRequest(String title, List<AnswerRequest> answers) {
        this.title = title;
        this.answers = answers;
    }
}

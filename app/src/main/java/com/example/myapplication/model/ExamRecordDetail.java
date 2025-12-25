package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class ExamRecordDetail {

    // 对应 question_id
    @SerializedName(value = "questionId", alternate = {"question_id"})
    public Long questionId;

    // 对应 user_answer (用户选了什么，如 "A" 或 "AB")
    @SerializedName(value = "userAnswer", alternate = {"user_answer"})
    public String userAnswer;

    // 对应 is_correct (1=对, 0=错)
    @SerializedName(value = "isCorrect", alternate = {"is_correct"})
    public boolean isCorrect;

    public ExamRecordDetail(Long questionId, String userAnswer, boolean isCorrect) {
        this.questionId = questionId;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }
}
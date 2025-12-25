package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Question {

    // 1. 题目ID
    @SerializedName(value = "id", alternate = {"question_id", "questionId"})
    public Long id;

    // 2. 题目内容
    @SerializedName("content")
    public String content;

    // 3. 题目类型
    @SerializedName(value = "type", alternate = {"type_id", "typeId"})
    public String type;

    // 4. 图片
    @SerializedName(value = "imageUrl", alternate = {"image_url", "imgUrl"})
    public String imageUrl;

    // 5. 解析
    @SerializedName(value = "explanation", alternate = {"analysis", "desc"})
    public String explanation;

    // 6. 接收 options 数组
    @SerializedName("options")
    public List<OptionInner> optionsList;

    // UI 字段
    public String optionA;
    public String optionB;
    public String optionC;
    public String optionD;
    public String answer;
    public String userAnswer;

    /**
     * 数据清洗方法
     */
    public void flattenOptions() {
        if (optionsList != null && !optionsList.isEmpty()) {
            // 1. 填充选项文本
            if (optionsList.size() > 0) optionA = optionsList.get(0).content;
            if (optionsList.size() > 1) optionB = optionsList.get(1).content;
            if (optionsList.size() > 2) optionC = optionsList.get(2).content;
            if (optionsList.size() > 3) optionD = optionsList.get(3).content;

            // 2. 提取正确答案
            StringBuilder sb = new StringBuilder();
            String[] labels = {"A", "B", "C", "D"};

            for (int i = 0; i < optionsList.size(); i++) {
                if (i >= labels.length) break;

                OptionInner opt = optionsList.get(i);

                // ★★★ 修改点 1：这里不再判断 == 1，而是直接判断 boolean ★★★
                if (opt.isCorrect) {
                    sb.append(labels[i]);
                }
            }
            this.answer = sb.toString();
        }
    }

    // 内部类
    public static class OptionInner {
        @SerializedName("content")
        public String content;

        // ★★★ 修改点 2：把 int 改为 boolean ★★★
        @SerializedName(value = "is_correct", alternate = {"isCorrect", "right", "answer"})
        public boolean isCorrect;
    }
}
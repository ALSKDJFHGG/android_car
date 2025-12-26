package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 题目数据模型
 * 表示考试题目，包含题目内容、选项、答案、类型等信息
 * 支持单选题、多选题和判断题
 *
 * @author 开发者
 * @version 1.0
 */
public class Question {

    /** 题目唯一标识ID */
    @SerializedName(value = "id", alternate = {"question_id", "questionId"})
    public Long id;

    /** 题目内容文本 */
    @SerializedName("content")
    public String content;

    /** 题目类型：1-单选题，2-判断题，3-多选题 */
    @SerializedName(value = "type", alternate = {"type_id", "typeId"})
    public String type;

    /** 题目图片URL，为空或"NULL"表示无图片 */
    @SerializedName(value = "imageUrl", alternate = {"image_url", "imgUrl"})
    public String imageUrl;

    /** 题目解析说明 */
    @SerializedName(value = "explanation", alternate = {"analysis", "desc"})
    public String explanation;

    /** 选项列表（从服务器接收的原始数据） */
    @SerializedName("options")
    public List<OptionInner> optionsList;

    // UI显示用字段（通过flattenOptions方法从optionsList转换得到）
    /** 选项A的内容 */
    public String optionA;
    /** 选项B的内容 */
    public String optionB;
    /** 选项C的内容 */
    public String optionC;
    /** 选项D的内容 */
    public String optionD;
    /** 正确答案（如"A"、"BC"等） */
    public String answer;
    /** 用户选择的答案 */
    public String userAnswer;

    /**
     * 数据清洗和转换方法
     * 将服务器返回的optionsList转换为UI可用的字段格式
     * 提取选项内容和正确答案
     */
    public void flattenOptions() {
        if (optionsList == null || optionsList.isEmpty()) {
            return;
        }

        // 填充选项文本内容
        populateOptionContents();

        // 提取正确答案
        extractCorrectAnswer();
    }

    /**
     * 填充选项内容到对应的字段中
     */
    private void populateOptionContents() {
        // 最多支持4个选项（A、B、C、D）
        if (optionsList.size() > 0) optionA = getSafeOptionContent(0);
        if (optionsList.size() > 1) optionB = getSafeOptionContent(1);
        if (optionsList.size() > 2) optionC = getSafeOptionContent(2);
        if (optionsList.size() > 3) optionD = getSafeOptionContent(3);
    }

    /**
     * 安全获取选项内容，避免空指针异常
     * @param index 选项索引
     * @return 选项内容，如果为空则返回空字符串
     */
    private String getSafeOptionContent(int index) {
        OptionInner option = optionsList.get(index);
        return option != null && option.content != null ? option.content : "";
    }

    /**
     * 从选项中提取正确答案
     * 多选题的答案会按字母顺序排序（如"ABC"）
     */
    private void extractCorrectAnswer() {
        StringBuilder answerBuilder = new StringBuilder();
        String[] labels = {"A", "B", "C", "D"};

        for (int i = 0; i < optionsList.size() && i < labels.length; i++) {
            OptionInner option = optionsList.get(i);
            if (option != null && option.isCorrect) {
                answerBuilder.append(labels[i]);
            }
        }

        this.answer = answerBuilder.toString();
    }

    /**
     * 选项内部类
     * 表示题目选项的原始数据结构
     */
    public static class OptionInner {
        /** 选项内容文本 */
        @SerializedName("content")
        public String content;

        /** 是否为正确答案 */
        @SerializedName(value = "is_correct", alternate = {"isCorrect", "right", "answer"})
        public boolean isCorrect;

        /**
         * 构造选项
         * @param content 选项内容
         * @param isCorrect 是否正确
         */
        public OptionInner(String content, boolean isCorrect) {
            this.content = content;
            this.isCorrect = isCorrect;
        }
    }
}
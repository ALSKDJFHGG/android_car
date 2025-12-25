package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExamRecord {

    // 1. 记录ID (主键)
    @SerializedName(value = "id", alternate = {"record_id", "recordId"})
    public Long id;

    // 2. 考生ID (外键)
    @SerializedName(value = "userId", alternate = {"user_id"})
    public Long userId;

    // 3. 试卷ID (外键)
    @SerializedName(value = "paperId", alternate = {"paper_id"})
    public Long paperId;

    // 4. 开始时间
    @SerializedName(value = "startTime", alternate = {"start_time"})
    public String startTime;

    // 5. 结束时间
    @SerializedName(value = "endTime", alternate = {"end_time"})
    public String endTime;

    // 6. 最终得分
    @SerializedName(value = "score", alternate = {"total_score", "totalScore"})
    public Double score;

    // 7. 是否及格
    @SerializedName(value = "passStatus", alternate = {"pass_status"})
    public Integer passStatus;

    // ★★★ 8. 新增：考试科目 ★★★
    // 映射数据库字段 subject (例如存 "科目一" 或 "1")
    @SerializedName(value = "subject", alternate = {"subject_name", "kemu"})
    public String subject;

    @SerializedName("detailList")
    public List<ExamRecordDetail> detailList;
    // --- 构造函数 ---

    public ExamRecord() {}

    /**
     * 用于提交成绩的构造函数 (已更新，增加 subject 参数)
     * @param userId 考生ID
     * @param score 分数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param subject 考试科目 (如 "科目一")
     */
    public ExamRecord(Long userId, Double score, String startTime, String endTime, String subject) {
        this.userId = userId;
        this.score = score;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.passStatus = (score != null && score >= 90) ? 1 : 0;
    }
}
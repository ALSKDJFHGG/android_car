package com.example.myapplication.model;

/**
 * 考试试卷数据模型
 * 表示一个可用的考试试卷信息
 *
 * @author 开发者
 * @version 1.0
 */
public class ExamPaper {

    /** 试卷ID */
    private int paperId;

    /** 试卷名称 */
    private String paperName;

    /** 考试科目（科目一/科目四） */
    private String subject;

    /** 题目总数 */
    private int totalQuestions;

    /** 考试时长（分钟） */
    private int examTime;

    /**
     * 默认构造函数
     */
    public ExamPaper() {
    }

    /**
     * 带参数的构造函数
     */
    public ExamPaper(int paperId, String paperName, String subject, int totalQuestions, int examTime) {
        this.paperId = paperId;
        this.paperName = paperName;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.examTime = examTime;
    }

    // Getter 和 Setter 方法

    public int getPaperId() {
        return paperId;
    }

    public void setPaperId(int paperId) {
        this.paperId = paperId;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getExamTime() {
        return examTime;
    }

    public void setExamTime(int examTime) {
        this.examTime = examTime;
    }

    /**
     * 获取格式化的考试时长显示
     * @return 格式化的时长字符串，如 "45分钟"
     */
    public String getFormattedExamTime() {
        return examTime + "分钟";
    }

    /**
     * 获取格式化的题目数量显示
     * @return 格式化的题目数量字符串，如 "100道题"
     */
    public String getFormattedQuestionCount() {
        return totalQuestions + "道题";
    }

    @Override
    public String toString() {
        return "ExamPaper{" +
                "paperId=" + paperId +
                ", paperName='" + paperName + '\'' +
                ", subject='" + subject + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", examTime=" + examTime +
                '}';
    }
}

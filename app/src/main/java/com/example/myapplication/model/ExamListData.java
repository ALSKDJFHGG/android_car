package com.example.myapplication.model;

import java.util.List;

/**
 * 考试列表数据包装类
 *
 * @author 开发者
 * @version 1.0
 */
public class ExamListData {
    /** 考试试卷列表 */
    private List<ExamPaper> list;

    public List<ExamPaper> getList() {
        return list;
    }

    public void setList(List<ExamPaper> list) {
        this.list = list;
    }
}

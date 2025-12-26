package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 主页面Activity
 * 提供驾考练习系统的功能入口，包括：
 * - 顺序练习
 * - 随机练习
 * - 模拟考试
 * - 错题本
 * - 考试记录
 * - 个人中心
 *
 * @author 开发者
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化所有UI控件并设置点击事件
        initViews();
    }

    /**
     * 初始化UI控件并设置点击监听器
     */
    private void initViews() {
        // 考试相关功能按钮
        findViewById(R.id.card_start_exam).setOnClickListener(this);      // 模拟考试
        findViewById(R.id.card_start_order_exam).setOnClickListener(this); // 顺序练习
        findViewById(R.id.card_start_random_exam).setOnClickListener(this); // 随机练习
        findViewById(R.id.card_wrong_book).setOnClickListener(this);       // 错题本
        findViewById(R.id.card_history).setOnClickListener(this);          // 考试记录

        // 个人中心按钮 - 使用独立的点击监听器
        findViewById(R.id.cv_user_profile).setOnClickListener(v -> {
            navigateToProfile();
        });
    }

    /**
     * 跳转到个人中心页面
     */
    private void navigateToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * 显示选择考试科目的对话框
     * @param examType 考试模式 ("order", "random", "exam")
     */
    private void showSubjectDialog(String examType) {
        // 定义可用的考试科目
        final String[] subjects = {"科目一", "科目四"};
        // 默认选中第一个科目
        final int[] checkedItem = {0};

        new AlertDialog.Builder(this)
                .setTitle("请选择考试科目")
                .setSingleChoiceItems(subjects, checkedItem[0], (dialog, which) -> {
                    // 更新用户选择的科目索引
                    checkedItem[0] = which;
                })
                .setPositiveButton("开始", (dialog, which) -> {
                    // 获取用户选择的科目
                    String selectedSubject = subjects[checkedItem[0]];
                    // 跳转到考试页面并传递参数
                    navigateToExam(examType, selectedSubject);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 跳转到考试页面
     * @param examType 考试类型
     * @param subject 考试科目
     */
    private void navigateToExam(String examType, String subject) {
        Intent intent = new Intent(this, ExamActivity.class);
        intent.putExtra("exam_type", examType);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        // 根据点击的视图ID处理不同功能
        if (viewId == R.id.card_start_exam) {
            // 模拟考试 - 需要选择科目
            showSubjectDialog("exam");
        } else if (viewId == R.id.card_start_order_exam) {
            // 顺序练习 - 需要选择科目
            showSubjectDialog("order");
        } else if (viewId == R.id.card_start_random_exam) {
            // 随机练习 - 需要选择科目
            showSubjectDialog("random");
        } else if (viewId == R.id.card_wrong_book) {
            // 错题本 - 直接跳转
            navigateToMistakeBook();
        } else if (viewId == R.id.card_history) {
            // 考试记录 - 直接跳转
            navigateToRecord();
        }
    }

    /**
     * 跳转到错题本页面
     */
    private void navigateToMistakeBook() {
        Intent intent = new Intent(this, MistakeActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到考试记录页面
     */
    private void navigateToRecord() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
}
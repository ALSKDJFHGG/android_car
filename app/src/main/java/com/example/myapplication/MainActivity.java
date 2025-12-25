package com.example.myapplication;

import android.app.AlertDialog; // 引入弹窗类
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 找到所有的 CardView (把它们当按钮用)
        View btnExam = findViewById(R.id.card_start_exam);
        View btnOrder = findViewById(R.id.card_start_order_exam);
        View btnRandom = findViewById(R.id.card_start_random_exam);
        View btnWrong = findViewById(R.id.card_wrong_book);
        View btnHistory = findViewById(R.id.card_history);

        // 2. 设置点击监听器
        btnExam.setOnClickListener(this);
        btnOrder.setOnClickListener(this);
        btnRandom.setOnClickListener(this);
        btnWrong.setOnClickListener(this);
        btnHistory.setOnClickListener(this);

        View btnProfile = findViewById(R.id.cv_user_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到个人中心
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 显示选择科目的弹窗
     * @param examType 考试模式 ("order", "random", "exam")
     */
    private void showSubjectDialog(String examType) {
        final String[] subjects = {"科目一", "科目四"};
        // 默认选中第0个（科目一）
        final int[] checkedItem = {0};

        new AlertDialog.Builder(this)
                .setTitle("请选择考试科目")
                .setSingleChoiceItems(subjects, checkedItem[0], (dialog, which) -> {
                    // 更新选中项
                    checkedItem[0] = which;
                })
                .setPositiveButton("开始", (dialog, which) -> {
                    // 获取选中的科目名称
                    String selectedSubject = subjects[checkedItem[0]];

                    // 跳转页面并传递参数
                    Intent intent = new Intent(MainActivity.this, ExamActivity.class);
                    intent.putExtra("exam_type", examType);
                    intent.putExtra("subject", selectedSubject); // ★★★ 传递科目 ★★★
                    startActivity(intent);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        // 根据点击的 ID 判断是哪个“按钮”
        if (v.getId() == R.id.card_start_exam) {
            // 全真模拟考试 -> 弹窗选科目
            showSubjectDialog("exam");

        } else if (v.getId() == R.id.card_start_order_exam) {
            // 顺序练习 -> 弹窗选科目
            showSubjectDialog("order");

        } else if (v.getId() == R.id.card_start_random_exam) {
            // 随机练习 -> 弹窗选科目
            showSubjectDialog("random");

        } else if (v.getId() == R.id.card_wrong_book) {
            // 错题本 -> 直接跳转
            Intent intent = new Intent(this, MistakeActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.card_history) {
            // 考试记录 -> 直接跳转
            Intent intent = new Intent(this, RecordActivity.class);
            startActivity(intent);
        }
    }
}
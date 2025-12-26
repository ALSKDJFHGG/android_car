package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.ExamListAdapter;
import com.example.myapplication.model.ExamListResponse;
import com.example.myapplication.model.ExamPaper;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.util.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 考试列表Activity
 * 显示所有可用的考试试卷列表
 *
 * @author 开发者
 * @version 1.0
 */
public class ExamListActivity extends AppCompatActivity {

    // UI控件
    private RecyclerView rvExamList;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    // 数据
    private ExamListAdapter adapter;
    private List<ExamPaper> examList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        initViews();
        setupRecyclerView();
        loadExamList();
    }

    /**
     * 初始化UI控件
     */
    private void initViews() {
        rvExamList = findViewById(R.id.rv_exam_list);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);

        // 设置标题栏返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("考试列表");
        }
    }

    /**
     * 设置RecyclerView
     */
    private void setupRecyclerView() {
        rvExamList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExamListAdapter(examList);
        rvExamList.setAdapter(adapter);

        // 设置点击监听器
        adapter.setOnItemClickListener(examPaper -> {
            Log.d("ExamListActivity", "onItemClick called, examPaper: " + (examPaper != null ? examPaper.getPaperName() : "null"));
            if (examPaper != null) {
                // 点击试卷时跳转到考试页面
                startExam(examPaper);
            } else {
                Log.e("ExamListActivity", "examPaper is null in click listener");
                Toast.makeText(this, "试卷信息错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 确保RecyclerView可以接收点击事件
        rvExamList.setClickable(true);
        rvExamList.setFocusable(false);
    }

    /**
     * 加载考试列表数据
     */
    private void loadExamList() {
        showLoading(true);

        RetrofitClient.getInstance().getApi().getExamList().enqueue(new Callback<ExamListResponse>() {
            @Override
            public void onResponse(Call<ExamListResponse> call, Response<ExamListResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ExamListResponse examResponse = response.body();
                    if (examResponse != null && examResponse.isSuccess() && examResponse.data != null) {
                        examList.clear();
                        if (examResponse.data.getList() != null) {
                            examList.addAll(examResponse.data.getList());
                            Log.d("ExamListActivity", "Loaded " + examList.size() + " exam papers");
                            for (ExamPaper paper : examList) {
                                Log.d("ExamListActivity", "Paper: " + paper.getPaperName() + ", ID: " + paper.getPaperId());
                            }
                        } else {
                            Log.w("ExamListActivity", "examResponse.data.getList() is null");
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyView();
                    } else {
                        String errorMsg = examResponse != null ? examResponse.msg : "未知错误";
                        Log.e("ExamListActivity", "Failed to load exam list: " + errorMsg);
                        showError("获取考试列表失败：" + errorMsg);
                    }
                } else {
                    showError("网络请求失败");
                }
            }

            @Override
            public void onFailure(Call<ExamListResponse> call, Throwable t) {
                showLoading(false);
                showError("网络错误：" + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    /**
     * 开始考试
     */
    private void startExam(ExamPaper examPaper) {
        if (examPaper == null) {
            Log.e("ExamListActivity", "startExam: examPaper is null");
            Toast.makeText(this, "试卷信息错误", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ExamListActivity", "Starting exam: " + examPaper.getPaperName() + ", ID: " + examPaper.getPaperId());
        Toast.makeText(this, "开始考试：" + examPaper.getPaperName(), Toast.LENGTH_SHORT).show();

        // 跳转到考试页面，传递试卷信息
        Intent intent = new Intent(this, ExamActivity.class);
        intent.putExtra("exam_type", Constants.Exam.MODE_EXAM); // 模拟考试模式
        intent.putExtra("subject", examPaper.getSubject());
        intent.putExtra("paper_id", examPaper.getPaperId());
        intent.putExtra("paper_name", examPaper.getPaperName());
        Log.d("ExamListActivity", "Starting ExamActivity with paper_id: " + examPaper.getPaperId());
        startActivity(intent);
    }

    /**
     * 显示/隐藏加载状态
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 更新空状态显示
     */
    private void updateEmptyView() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(examList.isEmpty() ? View.VISIBLE : View.GONE);
        }
        if (rvExamList != null) {
            rvExamList.setVisibility(examList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 显示错误信息
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        updateEmptyView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

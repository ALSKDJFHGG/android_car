package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.MistakeAdapter;
import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.Question;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.util.UserManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MistakeActivity extends AppCompatActivity {

    private TextView tvCount;
    private RecyclerView recyclerView;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistake);

        // 初始化控件
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        tvCount = findViewById(R.id.tv_mistake_count);
        recyclerView = findViewById(R.id.rv_mistake_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        // 防止重复请求
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        // 显示加载对话框
        showLoadingDialog("正在加载错题...");

        Long currentUserId = UserManager.getInstance(this).getUserId();
        RetrofitClient.getInstance().getApi().getWrongQuestions(currentUserId).enqueue(new Callback<BaseResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Question>>> call, Response<BaseResponse<List<Question>>> response) {
                // 隐藏加载对话框
                hideLoadingDialog();

                if (response.body() != null && response.body().isSuccess()) {
                    List<Question> list = response.body().data;

                    if (list != null && !list.isEmpty()) {
                        for (Question q : list) {
                            q.flattenOptions();
                        }

                        // 更新数量提示
                        tvCount.setText("共加载 " + list.size() + " 道错题");

                        // 适配器设置
                        MistakeAdapter adapter = new MistakeAdapter(list, question -> {
                            // 点击回调：弹出解析
                            showAnalysis(question);
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        tvCount.setText("暂无错题记录");
                        // 清空列表 (防止之前有数据残留)
                        recyclerView.setAdapter(null);
                    }
                } else {
                    tvCount.setText("获取失败：" + (response.body() != null ? response.body().msg : "未知错误"));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Question>>> call, Throwable t) {
                // 隐藏加载对话框
                hideLoadingDialog();
                Toast.makeText(MistakeActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示加载对话框
     * @param message 加载提示信息
     */
    private void showLoadingDialog(String message) {
        if (isFinishing()) {
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = new ProgressDialog(this);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
        }
        loadingDialog.setMessage(message);
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载对话框
     */
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            try {
                loadingDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }

    // 弹出解析对话框
    // 弹出详细解析对话框
    private void showAnalysis(Question q) {
        // 1. 拼接选项内容 (让用户看到 A、B 具体是什么)
        StringBuilder optionsBuilder = new StringBuilder();

        if (q.optionA != null) optionsBuilder.append("A. ").append(q.optionA).append("\n");
        if (q.optionB != null) optionsBuilder.append("B. ").append(q.optionB).append("\n");
        if (q.optionC != null && !"NULL".equalsIgnoreCase(q.optionC)) optionsBuilder.append("C. ").append(q.optionC).append("\n");
        if (q.optionD != null && !"NULL".equalsIgnoreCase(q.optionD)) optionsBuilder.append("D. ").append(q.optionD).append("\n");

        // 2. 准备其他信息
        String explanation = (q.explanation == null || q.explanation.isEmpty()) ? "暂无详细解析" : q.explanation;
        String myAns = (q.userAnswer == null) ? "无" : q.userAnswer;

        // 3. 构建完整的显示文本
        String message =
                "【选项详情】\n" + optionsBuilder.toString() +
                        "\n----------------\n" +
                        "【我的答案】 " + myAns + "\n" +
                        "【正确答案】 " + q.answer + "\n\n";

        // 4. 显示弹窗
        new AlertDialog.Builder(this)
                .setTitle("题目详情")
                .setMessage(message) // 显示拼接好的长文本
                .setPositiveButton("明白了", null)
                .show();
    }
}
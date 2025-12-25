package com.example.myapplication;

import android.app.AlertDialog;
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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MistakeActivity extends AppCompatActivity {

    private TextView tvCount;
    private RecyclerView recyclerView;

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
        // TODO: 实际开发中，请从 SharedPreferences 获取当前登录用户的 ID
        long currentUserId = 20; // 暂时写死测试

        RetrofitClient.getInstance().getApi().getWrongQuestions(currentUserId).enqueue(new Callback<BaseResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Question>>> call, Response<BaseResponse<List<Question>>> response) {
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
                Toast.makeText(MistakeActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 弹出解析对话框
    private void showAnalysis(Question q) {
        // 防止解析为空
        String explanation = (q.explanation == null || q.explanation.isEmpty()) ? "暂无详细解析" : q.explanation;

        new AlertDialog.Builder(this)
                .setTitle("题目解析")
                .setMessage("【正确答案】 " + q.answer + "\n\n【解析】\n" + explanation)
                .setPositiveButton("知道了", null)
                .show();
    }
}
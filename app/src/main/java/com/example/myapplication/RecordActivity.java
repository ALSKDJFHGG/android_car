package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.RecordAdapter;
import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.ExamRecord;
import com.example.myapplication.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rv_record_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        // TODO: 实际开发中，请从 SharedPreferences 获取当前登录用户的 ID
        long currentUserId = 20;

        RetrofitClient.getInstance().getApi().getExamRecords(currentUserId).enqueue(new Callback<BaseResponse<List<ExamRecord>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<ExamRecord>>> call, Response<BaseResponse<List<ExamRecord>>> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    List<ExamRecord> list = response.body().data;
                    if (list != null && !list.isEmpty()) {
                        recyclerView.setAdapter(new RecordAdapter(list));
                    } else {
                        Toast.makeText(RecordActivity.this, "暂无考试记录", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<ExamRecord>>> call, Throwable t) {
                Toast.makeText(RecordActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
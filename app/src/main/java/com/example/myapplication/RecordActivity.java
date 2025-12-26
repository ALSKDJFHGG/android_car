package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.adapter.RecordAdapter;
import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.ExamRecord;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.util.UserManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog loadingDialog;

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
        // 防止重复请求
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        // 显示加载对话框
        showLoadingDialog("正在加载考试记录...");

        Long currentUserId = UserManager.getInstance(this).getUserId();

        RetrofitClient.getInstance().getApi().getExamRecords(currentUserId).enqueue(new Callback<BaseResponse<List<ExamRecord>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<ExamRecord>>> call, Response<BaseResponse<List<ExamRecord>>> response) {
                // 隐藏加载对话框
                hideLoadingDialog();

                if (response.body() != null && response.body().isSuccess()) {
                    List<ExamRecord> list = response.body().data;
                    if (list != null && !list.isEmpty()) {
                        recyclerView.setAdapter(new RecordAdapter(list));
                    } else {
                        Toast.makeText(RecordActivity.this, "暂无考试记录", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = response.body() != null ? response.body().msg : "加载失败";
                    Toast.makeText(RecordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<ExamRecord>>> call, Throwable t) {
                // 隐藏加载对话框
                hideLoadingDialog();
                Toast.makeText(RecordActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}
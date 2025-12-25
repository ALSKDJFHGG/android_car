package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.User;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPhone, etPassword;
    private Button btnRegister;
    private TextView tvBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_reg_username);
        etPhone = findViewById(R.id.et_reg_phone);
        etPassword = findViewById(R.id.et_reg_password);
        btnRegister = findViewById(R.id.btn_register_submit);
        tvBackLogin = findViewById(R.id.tv_back_login);

        tvBackLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        // 1. 获取输入内容
        String name = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // 2. 简单的空值校验
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) ||
                 TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "请补全所有注册信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 构造符合接口要求的对象
        RegisterRequest request = new RegisterRequest(name, phone, pass);

        // 4. 发送网络请求
        RetrofitClient.getInstance().getApi().register(request).enqueue(new Callback<BaseResponse<User>>() {

            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                // 请求成功 (服务器有响应)
                if (response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();

                    // 注册成功后，关闭页面，让用户去登录
                    finish();
                } else {
                    // 服务器返回错误 (比如：手机号已存在)
                    String msg = (response.body() != null) ? response.body().msg : "注册失败，请重试";
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                // 网络错误 (没联网、服务器没开、IP填错)
                Toast.makeText(RegisterActivity.this, "网络连接失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}

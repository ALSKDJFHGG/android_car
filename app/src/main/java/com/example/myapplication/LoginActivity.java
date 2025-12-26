package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.User;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.util.UserManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etusername, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserManager.getInstance(this).isLoggedIn()) {
            // 1. 如果已经登录，直接跳转主页
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            // 2. 关闭登录页，防止按返回键回到登录页
            finish();
            return; // 3. 必须 return，不执行后面的 setContentView
        }
        setContentView(R.layout.activity_login);

        // 注意这里的 ID 变了
        etusername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> doLogin());

        // 跳转注册页
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void doLogin() {
        String username = etusername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "请输入手机号和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构造请求对象
        LoginRequest request = new LoginRequest(username, password);

        // 发起请求
        RetrofitClient.getInstance().getApi().login(request).enqueue(new Callback<BaseResponse<User>>() {

            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                User user = response.body().data;
                if (response.body() != null && response.body().isSuccess()) {
                    UserManager.getInstance(LoginActivity.this).saveUser(user);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().msg : "登录失败";
//                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                // 请求失败逻辑
                Toast.makeText(LoginActivity.this, "网络错误：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}

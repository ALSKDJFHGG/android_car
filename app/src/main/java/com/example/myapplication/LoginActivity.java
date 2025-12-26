package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private ProgressDialog loadingDialog;

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

        // 防止重复点击
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        Log.d("LoginActivity", "开始登录，用户名: " + username);

        // 显示加载对话框并禁用按钮
        showLoadingDialog("正在登录...");
        btnLogin.setEnabled(false);

        // 构造请求对象
        LoginRequest request = new LoginRequest(username, password);

        // 发起请求
        RetrofitClient.getInstance().getApi().login(request).enqueue(new Callback<BaseResponse<User>>() {

            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                // 隐藏加载对话框并启用按钮
                hideLoadingDialog();
                btnLogin.setEnabled(true);

                Log.d("LoginActivity", "收到登录响应，状态码: " + response.code());
                
                // 检查Activity是否还存在
                if (isFinishing()) {
                    Log.w("LoginActivity", "Activity正在销毁，忽略响应");
                    return;
                }

                // 检查HTTP响应状态码
                if (!response.isSuccessful()) {
                    String errorMsg = "请求失败，状态码：" + response.code();
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("LoginActivity", "错误响应体: " + errorBody);
                            errorMsg = errorBody;
                        } catch (Exception e) {
                            Log.e("LoginActivity", "读取错误响应体失败", e);
                        }
                    }
                    Log.e("LoginActivity", "登录失败: " + errorMsg);
                    showErrorToast(errorMsg);
                    return;
                }

                // 检查响应体是否为空
                if (response.body() == null) {
                    Log.e("LoginActivity", "响应体为空");
                    showErrorToast("服务器返回数据为空");
                    return;
                }

                BaseResponse<User> baseResponse = response.body();
                Log.d("LoginActivity", "响应码: " + baseResponse.code + ", 消息: " + baseResponse.msg);
                
                // 检查登录是否成功
                if (baseResponse.isSuccess() && baseResponse.data != null) {
                    // 登录成功
                    Log.d("LoginActivity", "登录成功，用户ID: " + baseResponse.data.id);
                    User user = baseResponse.data;
                    UserManager.getInstance(LoginActivity.this).saveUser(user);
                    showSuccessToast("登录成功");
                    
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 登录失败，显示错误信息
                    String errorMsg = baseResponse.msg != null && !baseResponse.msg.isEmpty() 
                            ? baseResponse.msg 
                            : "登录失败，请检查用户名和密码";
                    Log.e("LoginActivity", "登录失败: " + errorMsg + ", code: " + baseResponse.code);
                    showErrorToast(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                // 隐藏加载对话框并启用按钮
                hideLoadingDialog();
                btnLogin.setEnabled(true);

                Log.e("LoginActivity", "登录请求失败", t);
                
                // 检查Activity是否还存在
                if (isFinishing()) {
                    Log.w("LoginActivity", "Activity正在销毁，忽略失败响应");
                    return;
                }
                
                // 请求失败逻辑
                String errorMessage = t.getMessage() != null ? t.getMessage() : "未知网络错误";
                Log.e("LoginActivity", "网络错误: " + errorMessage);
                showErrorToast("网络错误：" + errorMessage);
            }
        });
    }

    /**
     * 安全地显示成功提示
     * 检查Activity状态，防止在Activity销毁后显示Toast导致崩溃
     * @param message 提示信息
     */
    private void showSuccessToast(String message) {
        if (isFinishing()) {
            return;
        }
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安全地显示错误提示
     * 检查Activity状态，防止在Activity销毁后显示Toast导致崩溃
     * @param message 错误信息
     */
    private void showErrorToast(String message) {
        if (isFinishing()) {
            return;
        }
        try {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

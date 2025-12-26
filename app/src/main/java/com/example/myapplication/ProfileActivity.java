package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.ChangePwdRequest;
import com.example.myapplication.model.User;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.util.UserManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
    }

    private void initViews() {
        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvPhone = findViewById(R.id.tv_profile_phone);
        Button btnChangePwd = findViewById(R.id.btn_change_pwd);
        Button btnLogout = findViewById(R.id.btn_logout);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 显示用户信息
        User currentUser = UserManager.getInstance(this).getUser();
        String displayName = currentUser.username;
        tvName.setText("用户名：" + displayName);
        tvPhone.setText("手机号：" + currentUser.phone);

        // ★★★ 点击修改密码 ★★★
        btnChangePwd.setOnClickListener(v -> showChangePwdDialog());

        // 退出登录
        btnLogout.setOnClickListener(v -> performLogout());
    }

    /**
     * 显示修改密码弹窗
     */
    private void showChangePwdDialog() {
        // 1. 加载自定义布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_pwd, null);
        final EditText etOld = view.findViewById(R.id.et_old_pwd);
        final EditText etNew = view.findViewById(R.id.et_new_pwd);
        final EditText etValid = view.findViewById(R.id.et_valid_pwd);

        // 2. 创建弹窗
        new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(view)
                .setPositiveButton("确认修改", null) // 这里先设为null，后面重写点击事件防止自动关闭
                .setNegativeButton("取消", null)
                .create();

        // 为了做输入校验，我们手动创建 Dialog 并覆盖 onClick
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(view)
                .setPositiveButton("确认", null) // 设为null，稍后覆盖
                .setNegativeButton("取消", null)
                .create();

        dialog.show();

        // 覆盖确认按钮的点击事件 (为了在校验失败时不关闭弹窗)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String oldPwd = etOld.getText().toString().trim();
            String newPwd = etNew.getText().toString().trim();
            String validPwd = etValid.getText().toString().trim();

            if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(newPwd) || TextUtils.isEmpty(validPwd)) {
                Toast.makeText(ProfileActivity.this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPwd.equals(validPwd)) {
                Toast.makeText(ProfileActivity.this, "两次新密码输入不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            // 发起请求
            doChangePassword(oldPwd, newPwd, validPwd, dialog);
        });
    }

    /**
     * 发送修改密码请求
     */
    private void doChangePassword(String oldPwd, String newPwd, String validPwd, AlertDialog dialog) {
        // 防止重复请求
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return;
        }

        // 显示加载对话框
        showLoadingDialog("正在修改密码...");

        long userId = UserManager.getInstance(this).getUserId();
        ChangePwdRequest request = new ChangePwdRequest(oldPwd, newPwd, validPwd);

        RetrofitClient.getInstance().getApi().changePassword(userId, request)
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        // 隐藏加载对话框
                        hideLoadingDialog();

                        if (response.body() != null && response.body().isSuccess()) {
                            // 修改成功
                            dialog.dismiss(); // 关闭弹窗
                            Toast.makeText(ProfileActivity.this, "密码修改成功，请重新登录", Toast.LENGTH_LONG).show();

                            // 强制退出登录
                            performLogout();
                        } else {
                            String msg = response.body() != null ? response.body().msg : "修改失败";
                            Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        // 隐藏加载对话框
                        hideLoadingDialog();
                        Toast.makeText(ProfileActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    /**
     * 执行退出登录逻辑
     */
    private void performLogout() {
        UserManager.getInstance(this).logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
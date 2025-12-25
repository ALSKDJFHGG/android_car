package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.myapplication.model.User;

public class UserManager {

    private static final String PREF_NAME = "driving_exam_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_REALNAME = "real_name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_IS_LOGIN = "is_login";

    private static UserManager instance;
    private final SharedPreferences sp;

    // 私有构造函数，单例模式
    private UserManager(Context context) {
        // 使用 ApplicationContext 防止内存泄漏
        sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 获取单例实例
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    /**
     * 保存用户信息 (登录成功时调用)
     */
    public void saveUser(User user) {
        SharedPreferences.Editor editor = sp.edit();
        if (user.id != null) editor.putLong(KEY_USER_ID, user.id);
        if (user.username != null) editor.putString(KEY_USERNAME, user.username);
        if (user.realName != null) editor.putString(KEY_REALNAME, user.realName);
        if (user.phone != null) editor.putString(KEY_PHONE, user.phone);

        editor.putBoolean(KEY_IS_LOGIN, true); // 标记已登录
        editor.apply(); // 异步提交，不卡主线程
    }

    /**
     * 获取当前登录的用户信息
     */
    public User getUser() {
        User user = new User();
        user.id = sp.getLong(KEY_USER_ID, -1); // 默认值为 -1
        user.username = sp.getString(KEY_USERNAME, "未登录");
        user.realName = sp.getString(KEY_REALNAME, "");
        user.phone = sp.getString(KEY_PHONE, "");
        return user;
    }

    /**
     * 单独获取用户ID (方便接口调用)
     */
    public long getUserId() {
        return sp.getLong(KEY_USER_ID, -1);
    }

    /**
     * 判断是否已登录
     */

    public boolean isLoggedIn() {
        return getUserId() != -1L;
    }
    /**
     * 退出登录 (清空数据)
     */
    public void logout() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear(); // 清空所有数据
        editor.apply();
    }
}
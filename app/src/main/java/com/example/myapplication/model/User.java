package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class User {

    // 1. 映射 JSON 里的 "userId" -> Java 的 "id"
    @SerializedName(value = "userId", alternate = {"id", "user_id"})
    public Long id;

    // 2. 映射 JSON 里的 "username"
    @SerializedName("username")
    public String username;

    // 3. 映射 JSON 里的 "realname" (全小写) -> Java 的 "realName"
    @SerializedName(value = "realname", alternate = {"realName", "real_name"})
    public String realName;

    // 4. 映射 JSON 里的 "phone"
    @SerializedName("phone")
    public String phone;

    // 5. ★★★ 重点修改：JSON 里是 "role": "admin" (字符串) ★★★
    // 你之前可能是 roleId (数字)，类型对不上会导致解析失败
    @SerializedName(value = "role", alternate = {"roleId"})
    public String role;

    // 6. 密码 (登录返回的 JSON 里通常没有密码，但注册时需要，保留即可)
    @SerializedName(value = "password", alternate = {"userPassword"})
    public String password;

    // 空构造函数 (必须有)
    public User() {
    }

    // 登录用的构造函数
    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
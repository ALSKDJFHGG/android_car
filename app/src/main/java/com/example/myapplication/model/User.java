package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class User {

    // 1. 用户ID (主键)
    // 映射数据库字段: user_id
    @SerializedName(value = "id", alternate = {"user_id", "userId"})
    public Long id;

    // 2. 用户名
    // 映射数据库字段: username
    // 兼容注册接口用的: userName
    @SerializedName(value = "username", alternate = {"userName"})
    public String username;

    // 3. 密码
    // 映射数据库字段: password
    // 兼容注册接口用的: userPassword
    @SerializedName(value = "password", alternate = {"userPassword"})
    public String password;

    // 4. 真实姓名
    // 映射数据库字段: real_name
    @SerializedName(value = "realName", alternate = {"real_name"})
    public String realName;

    // 5. 手机号
    // 映射数据库字段: phone
    // 兼容注册接口用的: userPhoneNumber
    @SerializedName(value = "phone", alternate = {"userPhoneNumber", "phoneNumber"})
    public String phone;

    // 6. 角色ID
    // 映射数据库字段: role_id (默认2=普通用户)
    @SerializedName(value = "roleId", alternate = {"role_id"})
    public Integer roleId;

    // 7. 状态
    // 映射数据库字段: status (1-正常, 0-禁用)
    @SerializedName("status")
    public Integer status;

    // 8. 创建时间
    // 映射数据库字段: create_time
    @SerializedName(value = "createTime", alternate = {"create_time"})
    public String createTime;

    // --- 构造函数 ---

    // 必须保留一个空构造函数，供 Gson 解析使用
    public User() {
    }

    // 方便注册时使用的构造函数
    public User(String username, String phone, String password, String realName) {
        this.username = username;
        this.phone = phone;
        this.password = password;
        this.realName = realName;
        // 默认给个角色ID，防止为空 (看你后端逻辑，如果后端处理了这里可以不写)
        this.roleId = 2;
        this.status = 1;
    }

    // 方便登录时使用的构造函数
    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

/**
 * 用户数据模型
 * 表示系统用户的完整信息
 *
 * @author 开发者
 * @version 1.0
 */
public class User {

    /** 用户唯一标识ID */
    @SerializedName(value = "userId", alternate = {"id", "user_id"})
    public Long id;

    /** 用户名 */
    @SerializedName("username")
    public String username;

    /** 真实姓名 */
    @SerializedName(value = "realname", alternate = {"realName", "real_name"})
    public String realName;

    /** 手机号码 */
    @SerializedName("phone")
    public String phone;

    /** 用户角色，如"admin"、"user"等 */
    @SerializedName(value = "role", alternate = {"roleId"})
    public String role;

    /** 用户密码（注册和登录时使用） */
    @SerializedName(value = "password", alternate = {"userPassword"})
    public String password;

    /**
     * 默认构造函数
     */
    public User() {
    }

    /**
     * 登录用的构造函数
     * @param phone 手机号码
     * @param password 密码
     */
    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    /**
     * 获取用户显示名称
     * 优先显示真实姓名，如果为空则显示用户名
     * @return 用户显示名称
     */
    public String getDisplayName() {
        return realName != null && !realName.trim().isEmpty() ? realName : username;
    }

    /**
     * 检查是否为管理员
     * @return true表示管理员，false表示普通用户
     */
    public boolean isAdmin() {
        return "admin".equals(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
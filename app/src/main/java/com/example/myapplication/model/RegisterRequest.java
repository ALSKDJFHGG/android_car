package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    // @SerializedName 确保生成的 JSON key 必须是括号里的字符串
    @SerializedName("username")
    public String userName;

    @SerializedName("phone")
    public String userPhoneNumber;

    @SerializedName("password")
    public String userPassword;

    public RegisterRequest(String userName, String userPhoneNumber, String userPassword) {
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userPassword = userPassword;
    }
}
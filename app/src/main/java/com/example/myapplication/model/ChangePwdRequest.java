package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class ChangePwdRequest {
    public String oldPwd;
    public String newPwd;
    public String validPwd;

    public ChangePwdRequest(String oldPwd, String newPwd, String validPwd) {
        this.oldPwd = oldPwd;
        this.newPwd = newPwd;
        this.validPwd = validPwd;
    }
}
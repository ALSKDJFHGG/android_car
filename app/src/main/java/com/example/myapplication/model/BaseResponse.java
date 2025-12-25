package com.example.myapplication.model;

public class BaseResponse<T> {
    public int code;      // 200 表示成功
    public String msg;    // 提示信息
    public T data;        // 具体数据

    // 判断接口请求是否成功
    public boolean isSuccess() {
        return code == 200;
    }
}
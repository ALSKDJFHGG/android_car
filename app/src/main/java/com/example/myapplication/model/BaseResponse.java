package com.example.myapplication.model;

/**
 * 网络请求基础响应模型
 * 统一封装API响应的数据结构
 *
 * @param <T> 响应数据的具体类型
 * @author 开发者
 * @version 1.0
 */
public class BaseResponse<T> {

    /** 响应状态码，200表示成功 */
    public int code;

    /** 响应消息，成功或错误信息 */
    public String msg;

    /** 响应数据，具体的业务数据 */
    public T data;

    /**
     * 判断接口请求是否成功
     * @return true表示请求成功，false表示请求失败
     */
    public boolean isSuccess() {
        return code == 200;
    }

    /**
     * 获取响应数据的字符串表示
     * @return 格式化的响应信息
     */
    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
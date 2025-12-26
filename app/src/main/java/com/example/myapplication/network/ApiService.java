package com.example.myapplication.network;

import com.example.myapplication.model.BaseResponse;
import com.example.myapplication.model.ChangePwdRequest;
import com.example.myapplication.model.ExamRecord;
import com.example.myapplication.model.LoginRequest;
import com.example.myapplication.model.Question;
import com.example.myapplication.model.RegisterRequest;
import com.example.myapplication.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API服务接口
 * 定义所有网络请求的接口方法
 * 使用Retrofit注解配置HTTP请求
 *
 * @author 开发者
 * @version 1.0
 */
public interface ApiService {

    /**
     * 用户登录
     * @param request 登录请求体，包含用户名和密码
     * @return 用户信息响应
     */
    @POST("/api/v1/mobile/auth/login")
    Call<BaseResponse<User>> login(@Body LoginRequest request);

    /**
     * 用户注册
     * @param request 注册请求体，包含用户注册信息
     * @return 用户信息响应
     */
    @POST("/api/v1/mobile/auth/register")
    Call<BaseResponse<User>> register(@Body RegisterRequest request);

    /**
     * 获取所有题目（顺序练习）
     * @param subject 考试科目（如"科目一"、"科目四"）
     * @return 题目列表响应
     */
    @GET("/api/v1/mobile/orderquestion")
    Call<BaseResponse<List<Question>>> getAllQuestions(@Query("subject") String subject);

    /**
     * 获取随机题目（随机练习/模拟考试）
     * @param subject 考试科目（如"科目一"、"科目四"）
     * @return 题目列表响应
     */
    @GET("/api/v1/mobile/questions")
    Call<BaseResponse<List<Question>>> getRandomQuestions(@Query("subject") String subject);

    /**
     * 获取错题本
     * @param userId 用户ID
     * @return 错题列表响应
     */
    @GET("/api/v1/mobile/wrong-questions")
    Call<BaseResponse<List<Question>>> getWrongQuestions(@Query("userId") Long userId);

    /**
     * 获取考试记录
     * @param userId 用户ID
     * @return 考试记录列表响应
     */
    @GET("/api/v1/mobile/records")
    Call<BaseResponse<List<ExamRecord>>> getExamRecords(@Query("userId") Long userId);

    /**
     * 提交考试成绩
     * @param record 考试记录，包含成绩和答题详情
     * @return 提交结果响应
     */
    @POST("/api/v1/mobile/submit")
    Call<BaseResponse<String>> submitScore(@Body ExamRecord record);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 密码修改请求体
     * @return 修改结果响应
     */
    @PUT("api/v1/mobile/auth/{id}")
    Call<BaseResponse<Object>> changePassword(@Path("id") Long userId, @Body ChangePwdRequest request);
}

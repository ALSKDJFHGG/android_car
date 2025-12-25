package com.example.myapplication.network;

import com.example.myapplication.model.BaseResponse;
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
import retrofit2.http.Query;

public interface ApiService {
    // 1. 登录接口
    @POST("/api/v1/mobile/auth/login")
    Call<BaseResponse<User>> login(@Body LoginRequest request);

    @POST("/api/v1/mobile/auth/register")
    Call<BaseResponse<User>> register(@Body RegisterRequest request);
    // 2. 获取所有题目（顺序练习）
    @GET("/api/v1/mobile/orderquestion")
    Call<BaseResponse<List<Question>>> getAllQuestions(@Query("subject") String subject);
    // 3. 获取随机题目（模拟考试）
    @GET("/api/v1/mobile/questions")
    Call<BaseResponse<List<Question>>> getRandomQuestions(@Query("subject") String subject);

    // 4. 获取错题本
    @GET("/api/v1/mobile/wrong-questions")
    Call<BaseResponse<List<Question>>> getWrongQuestions(@Query("userId") Long userId);

    // 5. 获取考试记录
    @GET("/api/v1/mobile/records")
    Call<BaseResponse<List<ExamRecord>>> getExamRecords(@Query("userId") Long userId);

    // 6. 提交成绩
    @POST("/api/v1/mobile/submit")
    Call<BaseResponse<String>> submitScore(@Body ExamRecord record);
}

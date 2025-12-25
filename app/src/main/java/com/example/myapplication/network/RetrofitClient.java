package com.example.myapplication.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // 记得改成你队友的 IP
    private static final String BASE_URL = "http://192.168.43.212:8080/";

    private static RetrofitClient instance;
    private ApiService apiService;

    private RetrofitClient() {
        // 1. 配置超时时间 (这里设置了 90 秒，足够查询几千条数据了)
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS) // 连接超时：连接上服务器的时间
                .readTimeout(90, TimeUnit.SECONDS)    // 读取超时：等待服务器返回数据的时间 (重点是这个)
                .writeTimeout(90, TimeUnit.SECONDS)   // 写入超时：上传数据的时间
                .build();

        // 2. 将配置好的 client 塞给 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // ★★★ 这一行非常重要 ★★★
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApi() {
        return apiService;
    }
}
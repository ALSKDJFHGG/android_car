package com.example.myapplication.network;

import com.example.myapplication.util.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit客户端配置类
 * 负责网络请求的配置和API服务的创建
 * 使用单例模式确保全局唯一实例
 *
 * @author 开发者
 * @version 1.0
 */
public class RetrofitClient {

    /** 单例实例 */
    private static RetrofitClient instance;

    /** API服务实例 */
    private ApiService apiService;

    /**
     * 私有构造函数，初始化Retrofit配置
     */
    private RetrofitClient() {
        // 创建HTTP客户端配置
        OkHttpClient client = createHttpClient();

        // 创建Retrofit实例
        Retrofit retrofit = createRetrofit(client);

        // 创建API服务实例
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 创建配置好的OkHttpClient实例
     * @return 配置完成的HTTP客户端
     */
    private OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(Constants.Network.TIMEOUT_SECONDS, TimeUnit.SECONDS)  // 连接超时
                .readTimeout(Constants.Network.TIMEOUT_SECONDS, TimeUnit.SECONDS)     // 读取超时（重点）
                .writeTimeout(Constants.Network.TIMEOUT_SECONDS, TimeUnit.SECONDS)    // 写入超时
                .build();
    }

    /**
     * 创建配置好的Retrofit实例
     * @param client HTTP客户端
     * @return 配置完成的Retrofit实例
     */
    private Retrofit createRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(Constants.Network.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 获取RetrofitClient单例实例
     * @return RetrofitClient实例
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * 获取API服务实例
     * @return ApiService实例
     */
    public ApiService getApi() {
        return apiService;
    }
}
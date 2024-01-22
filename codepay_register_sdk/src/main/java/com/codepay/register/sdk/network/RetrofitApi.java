package com.codepay.register.sdk.network;

import com.alibaba.fastjson.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author pupan
 * @date 2022/6/15
 */
public interface RetrofitApi {

    /**
     * 发送请求
     *
     * @param body
     * @return
     */
    @POST("user/init")
    Call<JSONObject> init(@Body RequestBody body);

    /**
     * 发送请求
     *
     * @param body
     * @return
     */
    @POST("user/sendMessage")
    Call<JSONObject> sendMessage(@Body RequestBody body);

    /**
     * 发送请求
     *
     * @param body
     * @return
     */
    @POST("user/query")
    Call<JSONObject> query(@Body RequestBody body);
}

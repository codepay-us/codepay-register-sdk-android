package com.codepay.register.sdk.network;

import com.alibaba.fastjson.JSONObject;
import com.codepay.register.sdk.client.payment.PaymentResponseParams;
import com.codepay.register.sdk.listener.ECRHubResponseCallBack;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * @author hugo
 * @date 2022/6/15
 */
public class RetrofitClient {
    private static RetrofitApi api;

    private static String url;

    public static RetrofitApi init(String serviceUrl) {
        url = serviceUrl;
        if (null == api) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS);
            OkHttpClient okHttpClient = builder.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .build();
            api = retrofit.create(RetrofitApi.class);
        }
        return api;
    }

    public static RetrofitApi init() {
        if (null == api) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS);
            OkHttpClient okHttpClient = builder.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .build();
            api = retrofit.create(RetrofitApi.class);
        }
        return api;
    }

    public static void initServer(JSONObject params, final ECRHubResponseCallBack callBack) {
        getApi().init(RetrofitClient.createJsonRequestBody(params)).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (!response.isSuccessful()) {
                    callBack.onError("" + response.code(), response.message());
                    return;
                }
                JSONObject result = response.body();
                callBack.onSuccess(JSONObject.parseObject(result.toString(), PaymentResponseParams.class));
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                callBack.onError("" + ExceptionHandler.getErrorCode(), ExceptionHandler.handleException(t));
            }
        });
    }

    public static void senMessage(JSONObject params, final ECRHubResponseCallBack callBack) {
        getApi().sendMessage(RetrofitClient.createJsonRequestBody(params)).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (!response.isSuccessful()) {
                    callBack.onError("" + response.code(), response.message());
                    return;
                }
                JSONObject result = response.body();
                callBack.onSuccess(JSONObject.parseObject(result.toString(), PaymentResponseParams.class));
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                callBack.onError("" + ExceptionHandler.getErrorCode(), ExceptionHandler.handleException(t));
            }
        });
    }

    public static void query(JSONObject params, final ECRHubResponseCallBack callBack) {
        getApi().query(RetrofitClient.createJsonRequestBody(params)).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (!response.isSuccessful()) {
                    callBack.onError("" + response.code(), response.message());
                    return;
                }
                JSONObject result = response.body();
                callBack.onSuccess(JSONObject.parseObject(result.toString(), PaymentResponseParams.class));
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                callBack.onError("" + ExceptionHandler.getErrorCode(), ExceptionHandler.handleException(t));
            }
        });
    }


    public static RetrofitApi getApi() {
        return init();
    }

    public static RequestBody createJsonRequestBody(JSONObject data) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data.toJSONString());
    }
}

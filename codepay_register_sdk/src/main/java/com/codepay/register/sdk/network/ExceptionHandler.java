package com.codepay.register.sdk.network;

import com.alibaba.fastjson.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * exception handler
 * User: pupan
 * Date: 2022/6/29
 * Time: 15:10
 *
 * @author pupan
 */
public class ExceptionHandler {
    private static int errorCode = ErrorStatus.UNKNOWN_ERROR;
    private static String errorMsg = "Network connection timeout";

    /**
     * 获取错误代码
     */
    public static int getErrorCode() {
        return errorCode;
    }

    /***
     * 处理异常类型
     *@param e
     */
    public static String handleException(Throwable e) {
        e.printStackTrace();
        if (e instanceof SocketTimeoutException) {
            //网络超时
            errorMsg = "Network connection timeout";
            errorCode = ErrorStatus.NETWORK_ERROR;
        } else if (e instanceof ConnectException) {
            //均视为网络错误
            errorMsg = "Network Connection Exception";
            errorCode = ErrorStatus.NETWORK_ERROR;
        } else if (e instanceof JSONException
                || e instanceof ParseException) {
            errorMsg = "Data parsing exception";
            errorCode = ErrorStatus.SERVER_ERROR;
        } else if (e instanceof UnknownHostException) {
            errorMsg = "Network Connection Exception";
            errorCode = ErrorStatus.NETWORK_ERROR;
        } else if (e instanceof IllegalArgumentException) {
            errorMsg = "Parameter error";
            errorCode = ErrorStatus.SERVER_ERROR;
        } else {
            errorMsg = "Connection failure: error code";
            errorCode = ErrorStatus.SERVER_ERROR;
        }
        return errorMsg;
    }
}

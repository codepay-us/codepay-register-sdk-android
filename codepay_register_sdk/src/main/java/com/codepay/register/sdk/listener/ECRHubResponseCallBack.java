package com.codepay.register.sdk.listener;

/**
 * api return callback
 * User: pupan
 * Date: 2022/6/28
 * Time: 14:18
 *
 * @author pupan
 */
public interface ECRHubResponseCallBack {

    /**
     * Failed callback (1. The error callback and interface request caused by network exception are successful,
     * 2. The error callback returned when errorcode is not 0)
     *
     * @param errorCode error code
     * @param errorMsg  error msg
     */
    void onError(String errorCode, String errorMsg);

    /**
     * 1. Successful callback
     * 2. The returned data may be empty. Success only means that the returned errorcode is 0
     *
     * @param data response
     */
    void onSuccess(String data);
}

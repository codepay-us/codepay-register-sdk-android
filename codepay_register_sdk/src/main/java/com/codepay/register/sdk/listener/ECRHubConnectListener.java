package com.codepay.register.sdk.listener;

/**
 * connect status listener
 */
public interface ECRHubConnectListener {

    /**
     * connect successfully
     */
    void onConnect();

    /**
     * connect failed
     */
    void onDisconnect();

    /**
     * connect error
     *
     * @param errorCode error code
     * @param errorMsg  error message
     */
    void onError(String errorCode, String errorMsg);
}

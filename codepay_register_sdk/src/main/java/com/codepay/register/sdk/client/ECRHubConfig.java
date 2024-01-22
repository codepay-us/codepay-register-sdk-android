package com.codepay.register.sdk.client;

public class ECRHubConfig {
    /**
     * connect timeOut
     */
    private int connectionTimeout;

    /**
     * message timeout
     */
    private int messageTimeout;

    /**
     * auto connect
     */
    private boolean autoConnect;


    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setMessageTimeout(int messageTimeout) {
        this.messageTimeout = messageTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public boolean getAutoConnect() {
        return autoConnect;
    }

    public int getMessageTimeout() {
        return messageTimeout;
    }
}

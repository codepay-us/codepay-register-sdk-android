package com.codepay.register.sdk.listener;

import com.codepay.register.sdk.util.ECRHubMessageData;

/**
 * Device Pairing Listening
 */
public interface ECRHubPairListener {
    /**
     * The ip address of the device
     *
     * @param ip
     * @return
     */
    void onDevicePair(ECRHubMessageData data,String ip);


    /**
     * unpair
     */
    void onDeviceUnpair(ECRHubMessageData data);
}

package com.codepay.register.sdk.client;

import static com.codepay.register.sdk.util.Constants.BATCH_CLOSE_TOPIC;
import static com.codepay.register.sdk.util.Constants.ECR_HUB_TOPIC_PAIR;
import static com.codepay.register.sdk.util.Constants.ECR_HUB_TOPIC_UNPAIR;
import static com.codepay.register.sdk.util.Constants.HEART_BEAT_TOPIC;
import static com.codepay.register.sdk.util.Constants.QUERY_TOPIC;
import static com.codepay.register.sdk.util.Constants.TIP_ADJUSTMENT_TOPIC;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.codepay.register.sdk.client.payment.Payment;
import com.codepay.register.sdk.client.payment.WlanPayment;
import com.codepay.register.sdk.client.payment.PaymentResponseParams;
import com.codepay.register.sdk.device.ECRHubDevice;
import com.codepay.register.sdk.listener.ECRHubConnectListener;
import com.codepay.register.sdk.listener.ECRHubResponseCallBack;
import com.codepay.register.sdk.util.Constants;
import com.codepay.register.sdk.util.ECRHubMessageData;
import com.codepay.register.sdk.util.NetUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Objects;

/**
 * @author pupan
 */
public class ECRHubClient {

    private static final String TAG = "ECRHubClient";
    //server config
    private ECRHubConfig config;
    // context
    private Context context;
    // listener
    private ECRHubConnectListener connectListener;
    // server ip
    private String ipAddress;
    private WebSocketClient webSocketClient;
    public Payment payment;
    ECRHubResponseCallBack pairCallBack;

    private static ECRHubClient instance;

    private Constants.ECRHubType type = Constants.ECRHubType.WLAN;

    public static ECRHubClient getInstance() {
        if (null == instance) {
            instance = new ECRHubClient();
        }
        return instance;
    }

    public void init(ECRHubConfig config, ECRHubConnectListener listener, Context context, Constants.ECRHubType type) {
        this.context = context;
        this.config = config;
        this.connectListener = listener;
        this.type = type;
    }

    public void requestUnPair(ECRHubDevice deviceData, ECRHubResponseCallBack callBack) {
        pairCallBack = callBack;
        if (isConnected()) {
            ECRHubMessageData data = new ECRHubMessageData();
            data.getDevice_data().setDevice_name(deviceData.getName());
            data.getDevice_data().setMac_address(deviceData.getWs_address());
            data.getDevice_data().setPort(deviceData.getPort());
            data.getDevice_data().setIp_address(deviceData.getIp_address());
            data.getDevice_data().setAlias_name(deviceData.getName());
            data.setTopic(ECR_HUB_TOPIC_UNPAIR);
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    /**
     * 初始化websocket连接
     */
    private void initSocketClient() {
        URI uri;
        if (null != context) {
            uri = URI.create(ipAddress + "/" + "mac_address=" + NetUtils.getMacAddress(context));
        } else {
            uri = URI.create(ipAddress);
        }
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                PaymentResponseParams data = JSON.parseObject(message, PaymentResponseParams.class);
                if (data.getTopic().equals(ECR_HUB_TOPIC_PAIR) || data.getTopic().equals(ECR_HUB_TOPIC_UNPAIR)) {
                    pairCallBack.onSuccess(data);
                } else if (!data.getTopic().equals(HEART_BEAT_TOPIC)) {
                    String transType = data.getBiz_data().getTrans_type();
                    if (null == transType || transType.isEmpty() || Objects.equals(data.getTopic(), QUERY_TOPIC) || Objects.equals(data.getTopic(), BATCH_CLOSE_TOPIC) || Objects.equals(data.getTopic(), TIP_ADJUSTMENT_TOPIC)) {
                        transType = data.getTopic();
                    }
                    if (null != payment && null != payment.getResponseCallBack(transType)) {
                        payment.getResponseCallBack(transType).onSuccess(data);
                        payment.removeCurrentMessageData(transType);
                    }
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.e(TAG, "websocket connect success");
                if (null != connectListener) {
                    connectListener.onConnect();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "websocket connect error：" + ex);
                disConnect();
                if (null != connectListener) {
                    connectListener.onError(ex.getLocalizedMessage(), ex.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(TAG, "websocket disconect：·code:" + code + "·reason:" + reason + "·remote:" + remote);
                if (null != payment.getCurrentMessageData()) {
                    ECRHubMessageData data = payment.getCurrentMessageData();
                    data.setResponse_code("109");
                    data.setResponse_msg("Network anomaly");
                    String transType = data.getBiz_data().getTrans_type();
                    if (null == transType || transType.isEmpty() || Objects.equals(data.getTopic(), QUERY_TOPIC) || Objects.equals(data.getTopic(), BATCH_CLOSE_TOPIC) || Objects.equals(data.getTopic(), TIP_ADJUSTMENT_TOPIC)) {
                        transType = data.getTopic();
                    }
                    if (null != payment && null != payment.getResponseCallBack(transType)) {
                        PaymentResponseParams responseData = JSON.parseObject(JSON.toJSON(data).toString(), PaymentResponseParams.class);
                        payment.getResponseCallBack(transType).onSuccess(responseData);
                        payment.removeCurrentMessageData(transType);
                    }
                }
                if ((code != 1000 && code != -1) || remote) {
                    if (null != connectListener) {
                        connectListener.onDisconnect();
                    }
                    Log.e(TAG, "reconnect");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                reconnect();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        };
        if (config.getConnectionTimeout() != 0) {
            webSocketClient.setConnectionLostTimeout(config.getConnectionTimeout());
        } else {
            webSocketClient.setConnectionLostTimeout(5);
        }
        payment = new WlanPayment((webSocketClient));
    }

    public boolean isConnected() {
        return null != webSocketClient && webSocketClient.isOpen();
    }

    /**
     * connect
     */
    private void connectWlanServer() {
        if (null == webSocketClient) {
            initSocketClient();
        }
        if (webSocketClient.isOpen()) {
            if (null != connectListener) {
                connectListener.onConnect();
            }
            return;
        }
        if (webSocketClient.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
            webSocketClient.connect();
        } else if (webSocketClient.getReadyState().equals(ReadyState.CLOSING) || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
            webSocketClient.reconnect();
        }
    }

    public void connect(String ip) {
        if (type == Constants.ECRHubType.WLAN) {
            if (isConnected()) {
                disConnect();
            } else {
                webSocketClient = null;
            }
            ipAddress = ip;
            connectWlanServer();
        }
    }

    public void connect() {
        if (type == Constants.ECRHubType.WLAN) {
            if (isConnected()) {
                disConnect();
            } else {
                webSocketClient = null;
            }
            connectWlanServer();
        }
    }

    public void disConnect() {
        if (type == Constants.ECRHubType.WLAN) {
            if (null == webSocketClient || !webSocketClient.isOpen()) {
                return;
            }
            webSocketClient.close();
            webSocketClient = null;
        }
    }
}

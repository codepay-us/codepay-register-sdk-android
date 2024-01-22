package com.codepay.register.sdk.client.payment;

import static com.codepay.register.sdk.util.Constants.CLOSE_TOPIC;
import static com.codepay.register.sdk.util.Constants.PAYMENT_TOPIC;
import static com.codepay.register.sdk.util.Constants.QUERY_TOPIC;

import com.alibaba.fastjson.JSON;
import com.codepay.register.sdk.listener.ECRHubResponseCallBack;
import com.codepay.register.sdk.util.Constants;
import com.codepay.register.sdk.util.ECRHubMessageData;

import org.java_websocket.client.WebSocketClient;

public class Payment {
    WebSocketClient webSocketClient;
    private ECRHubResponseCallBack responseCallBack;

    public Payment(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public ECRHubResponseCallBack getResponseCallBack() {
        return responseCallBack;
    }

    public void purchase(PaymentParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        if (null == params.transType) {
            params.setTransType(Constants.TRANS_TYPE_PURCHASE);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setMerchant_order_no(params.merchantOrderNo);
        data.getBiz_data().setTrans_type("" + params.transType);
        data.getBiz_data().setOrder_amount(params.transAmount);
        data.getBiz_data().setConfirm_on_terminal(true);
        data.setRequest_id(params.msgId);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getAppId());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void close(PaymentParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(CLOSE_TOPIC);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setMerchant_order_no(params.merchantOrderNo);
        data.getBiz_data().setTrans_type("" + params.transType);
        data.getBiz_data().setOrder_amount(params.transAmount);
        data.getBiz_data().setConfirm_on_terminal(true);
        data.setRequest_id(params.msgId);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getAppId());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void query(PaymentParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(QUERY_TOPIC);
        }
        if (null == params.merchantOrderNo) {
            params.setMerchantOrderNo(params.origMerchantOrderNo);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.getOrigMerchantOrderNo());
        data.getBiz_data().setMerchant_order_no(params.merchantOrderNo);
        data.getBiz_data().setTrans_type("" + params.transType);
        data.getBiz_data().setOrder_amount(params.transAmount);
        data.getBiz_data().setConfirm_on_terminal(true);
        data.setRequest_id(params.msgId);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getAppId());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void refund(PaymentParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        if (null == params.transType) {
            params.setTransType(Constants.TRANS_TYPE_REFUND);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.getOrigMerchantOrderNo());
        data.getBiz_data().setMerchant_order_no(params.merchantOrderNo);
        data.getBiz_data().setTrans_type("" + params.transType);
        data.getBiz_data().setOrder_amount(params.transAmount);
        data.getBiz_data().setConfirm_on_terminal(true);
        data.setRequest_id(params.msgId);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getAppId());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }
}

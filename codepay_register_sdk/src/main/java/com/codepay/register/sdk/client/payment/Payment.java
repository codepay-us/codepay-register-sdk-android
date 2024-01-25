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

    public void purchase(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        if (null == params.trans_type) {
            params.setTrans_type(Constants.TRANS_TYPE_PURCHASE);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        data.getBiz_data().setConfirm_on_terminal(false);
        data.getBiz_data().setPay_scenario("SWIPE_CARD");
        data.setRequest_id(params.msg_id);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getApp_id());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void close(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(CLOSE_TOPIC);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        data.getBiz_data().setConfirm_on_terminal(true);
        data.setRequest_id(params.msg_id);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getApp_id());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void query(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(QUERY_TOPIC);
        }
        if (null == params.merchant_order_no) {
            params.setMerchant_order_no(params.orig_merchant_order_no);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.getOrig_merchant_order_no());
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        data.getBiz_data().setConfirm_on_terminal(true);
        data.setRequest_id(params.msg_id);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getApp_id());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void refund(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        if (null == params.trans_type) {
            params.setTrans_type(Constants.TRANS_TYPE_REFUND);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.getOrig_merchant_order_no());
        data.getBiz_data().setPay_method_category(params.getPay_method_id());
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        data.getBiz_data().setConfirm_on_terminal(false);
        data.setRequest_id(params.msg_id);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getApp_id());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void cancel(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        if (null == params.trans_type) {
            params.setTrans_type(Constants.TRANS_TYPE_VOID);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.getOrig_merchant_order_no());
        data.getBiz_data().setPay_method_category(params.getPay_method_id());
        data.getBiz_data().setMerchant_order_no(params.getMerchant_order_no());
        data.getBiz_data().setTrans_type("" + params.trans_type);
//        data.getBiz_data().setOrder_amount(params.transAmount);
        data.getBiz_data().setConfirm_on_terminal(false);
        data.setRequest_id(params.msg_id);
        data.setTopic(params.getTopic());
        data.setApp_id(params.getApp_id());
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

}

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

    public void sale(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        if (null == params.getCash_amount()) {
            params.setTrans_type(Constants.TRANS_TYPE_SALE);
        } else {
            params.setTrans_type(Constants.TRANS_TYPE_CASH_BACK);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        if (null != params.getCash_amount()) {
            data.getBiz_data().setCashback_amount(params.cash_amount);
        }
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        if (null != params.getOn_screen_tip()) {
            data.getBiz_data().setOn_screen_tip(params.on_screen_tip);
        }
        if (null != params.getConfirm_on_terminal()) {
            data.getBiz_data().setConfirm_on_terminal(params.confirm_on_terminal);
        }
        if (null != params.getPay_scenario()) {
            data.getBiz_data().setPay_scenario(params.pay_scenario);
        } else {
            data.getBiz_data().setPay_scenario("SWIPE_CARD");
        }
        if (null != params.getPrint_receipt()) {
            data.getBiz_data().setPrint_receipt(params.print_receipt);
        }
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void close(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(CLOSE_TOPIC);
        }
        if (null == params.merchant_order_no) {
            params.setMerchant_order_no(params.orig_merchant_order_no);
        }
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.merchant_order_no);
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
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
        data.getBiz_data().setOrig_merchant_order_no(params.merchant_order_no);
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void refund(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        params.setTrans_type(Constants.TRANS_TYPE_REFUND);
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.orig_merchant_order_no);
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        if (null != params.getPay_scenario()) {
            data.getBiz_data().setPay_scenario(params.pay_scenario);
        } else {
            data.getBiz_data().setPay_scenario("SWIPE_CARD");
        }
        if (null != params.getOn_screen_tip()) {
            data.getBiz_data().setOn_screen_tip(params.on_screen_tip);
        }
        if (null != params.getConfirm_on_terminal()) {
            data.getBiz_data().setConfirm_on_terminal(params.confirm_on_terminal);
        }
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void cancel(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        params.setTrans_type(Constants.TRANS_TYPE_VOID);
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setOrig_merchant_order_no(params.orig_merchant_order_no);
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        if (null != params.getPay_scenario()) {
            data.getBiz_data().setPay_scenario(params.pay_scenario);
        } else {
            data.getBiz_data().setPay_scenario("SWIPE_CARD");
        }
        if (null != params.getConfirm_on_terminal()) {
            data.getBiz_data().setConfirm_on_terminal(params.confirm_on_terminal);
        }
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void auth(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        params.setTrans_type(Constants.TRANS_TYPE_PRE_AUTH);
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        if (null != params.getOn_screen_tip()) {
            data.getBiz_data().setOn_screen_tip(params.on_screen_tip);
        }
        if (null != params.getConfirm_on_terminal()) {
            data.getBiz_data().setConfirm_on_terminal(params.confirm_on_terminal);
        }
        if (null != params.getPay_scenario()) {
            data.getBiz_data().setPay_scenario(params.pay_scenario);
        } else {
            data.getBiz_data().setPay_scenario("SWIPE_CARD");
        }
        data.getBiz_data().setPay_scenario(params.pay_scenario);
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

    public void completion(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        responseCallBack = callBack;
        if (null == params.getTopic()) {
            params.setTopic(PAYMENT_TOPIC);
        }
        params.setTrans_type(Constants.TRANS_TYPE_PRE_AUTH_COMPLETE);
        ECRHubMessageData data = new ECRHubMessageData();
        if (null != params.getVoice_data() && null != params.getVoice_data().getContent()) {
            data.getVoice_data().setContent(params.getVoice_data().getContent());
            data.getVoice_data().setContent_locale(params.getVoice_data().getContent_locale());
        }
        data.getBiz_data().setMerchant_order_no(params.merchant_order_no);
        data.getBiz_data().setOrig_merchant_order_no(params.orig_merchant_order_no);
        data.getBiz_data().setTrans_type("" + params.trans_type);
        data.getBiz_data().setOrder_amount(params.order_amount);
        if (null != params.getConfirm_on_terminal()) {
            data.getBiz_data().setConfirm_on_terminal(params.confirm_on_terminal);
        }
        if (null != params.getPay_scenario()) {
            data.getBiz_data().setPay_scenario(params.pay_scenario);
        } else {
            data.getBiz_data().setPay_scenario("SWIPE_CARD");
        }
        data.setRequest_id("111111");
        data.setTopic(params.getTopic());
        data.setApp_id(params.app_id);
        if (null != webSocketClient && webSocketClient.isOpen()) {
            webSocketClient.send(JSON.toJSON(data).toString());
        }
    }

}

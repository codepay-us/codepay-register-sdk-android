package com.codepay.register.sdk.client.payment;

import static com.codepay.register.sdk.util.Constants.CLOSE_TOPIC;
import static com.codepay.register.sdk.util.Constants.INIT_TOPIC;
import static com.codepay.register.sdk.util.Constants.PAYMENT_TOPIC;
import static com.codepay.register.sdk.util.Constants.QUERY_TOPIC;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.codepay.register.sdk.listener.ECRHubResponseCallBack;
import com.codepay.register.sdk.util.Constants;
import com.codepay.register.sdk.util.ECRHubMessageData;
import com.wiseecr.host.sdk.cdc.EcrCdcHost;
import com.wiseecr.host.sdk.common.ConnectionStatus;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UsbPayment extends Payment {

    private EcrCdcHost ecrCdcHost;
    Map<String, ECRHubResponseCallBack> callBackHashMap = new HashMap<>();

    public UsbPayment(EcrCdcHost ecrCdcHost) {
        this.ecrCdcHost = ecrCdcHost;
    }


    public ECRHubResponseCallBack getResponseCallBack(String transType) {
        return callBackHashMap.get(transType);
    }

    @Override
    public void init(ECRHubResponseCallBack callBack) {
        ECRHubMessageData data = new ECRHubMessageData();
        data.setRequest_id("111111");
        data.setTopic(INIT_TOPIC);
        callBackHashMap.put(INIT_TOPIC, callBack);
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }

    private void addCallBack(String transType, ECRHubResponseCallBack callBack) {
        if (callBackHashMap.containsKey(transType)) {
            callBackHashMap.remove(transType);
        }
        callBackHashMap.put(transType, callBack);
    }

    @Override
    public void sale(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(Constants.TRANS_TYPE_SALE, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            Log.e("UsbPayment", "发送数据" + data.toString());
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            Log.e("UsbPayment", "发送数据" + new String(request));
            int ret = ecrCdcHost.sendRawData(request, request.length);
            Log.e("UsbPayment", "发送数据结果" + ret);
        }
    }

    @Override
    public void close(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(CLOSE_TOPIC, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }

    @Override
    public void query(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(QUERY_TOPIC, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }

    @Override
    public void refund(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(Constants.TRANS_TYPE_REFUND, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }

    @Override
    public void cancel(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(Constants.TRANS_TYPE_VOID, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }

    @Override
    public void auth(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(Constants.TRANS_TYPE_PRE_AUTH, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }

    @Override
    public void completion(PaymentRequestParams params, ECRHubResponseCallBack callBack) {
        addCallBack(Constants.TRANS_TYPE_PRE_AUTH_COMPLETE, callBack);
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
        if (null != ecrCdcHost && ecrCdcHost.getConnectionStatus() == ConnectionStatus.STATUS_PORT_CONNECTED) {
            byte[] request = JSON.toJSON(data).toString().getBytes(StandardCharsets.UTF_8);
            ecrCdcHost.sendRawData(request, request.length);
        }
    }
}

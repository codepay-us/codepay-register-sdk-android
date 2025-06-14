package com.codepay.register.sdk.client.payment;

import com.codepay.register.sdk.listener.ECRHubResponseCallBack;
import com.codepay.register.sdk.util.ECRHubMessageData;

/**
 * payment
 */
public abstract class Payment {
    public abstract void removeCurrentMessageData(String transType);

    public abstract ECRHubMessageData getCurrentMessageData();
    public abstract ECRHubResponseCallBack getResponseCallBack(String transType);

    public abstract void sale(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void close(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void query(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void refund(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void cancel(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void auth(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void completion(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void batchClose(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void tipAdjustment(PaymentRequestParams params, ECRHubResponseCallBack callBack);

    public abstract void init(PaymentRequestParams params, ECRHubResponseCallBack callBack);
}

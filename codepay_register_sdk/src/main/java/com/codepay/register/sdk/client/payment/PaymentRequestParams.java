package com.codepay.register.sdk.client.payment;

import com.alibaba.fastjson.JSONObject;

public class PaymentRequestParams {
    /**
     *  topic
     */
    String topic;
    /**
     * The App ID assigned by the system to the caller must be passed in non-offline mode.
     */
    String app_id;
    /**
     * trans type
     */
    String trans_type;
    /**
     * Merchant order number.
     */
    String merchant_order_no;
    /**
     * Orig merchant order number
     */
    String orig_merchant_order_no;
    /**
     * Price Currency, compliant with ISO-4217 standard, described with a three-character code
     */
    String price_currency;
    /**
     * Order amount.  For example, one USD stands for one dollar, not one cent.
     */
    String order_amount;
    /**
     * Tip amount. This field represents the transaction tip amount. For example, 1 USD stands for one dollar, not one cent.
     * Example: 3.50
     */
    String tip_amount;
    /**
     * Whether or not to enter tips on the CodePay Register page, default is false, when "trans_type=1, 3, 4", this parameter can be set.
     */
    Boolean on_screen_tip;
    /**
     * Cashback amount. Expressed in the quoted currency, for example, One USD stands for one dollar, not one cent.
     */
    String cash_amount;
    /**
     * Specify a payment method. This field is mandatory only when "pay_scenario" is set to "SCANQR_PAY" or "BSCANQR_PAY".
     */
    String pay_method_id;
    /**
     * Payment scene
     * SWIPE_CARD	Bank card payment
     * SCANQR_PAY	QR Pay(Customer-presented)
     * BSCANQR_PAY	QR Pay(Customer-presented)
     */
    String pay_scenario;
    /**
     * Bank card network, when this parameter is not required, the bank card transaction selects the network based on the type of card being read
     */
    String card_network_type;
    /**
     * attach info
     */
    String attach;
    /**
     * description info
     */
    String description;
    /**
     * Callback address for payment notification.
     * Receive payment notifications from the Gateway to call back the server address, and only when the transaction goes through the payment gateway will there be a callback.
     * Example: http://www.abc.com/callback?id=12345
     */
    String notify_url;
    /**
     * Order expires time, after the expires time is not allowed to be paid, unit: seconds.
     */
    String expires;
    /**
     * When refund or void a transaction, does the store manager role need to authorize this operation on the terminal? default value: false
     */
    Boolean required_terminal_authentication;
    /**
     * This parameter controls the display logic of electronic
     */
    Boolean on_screen_signature;

    Boolean confirm_on_terminal;

    Integer receipt_print_mode;

    String cashier;

    String tip_adjustment_amount;

    private String card_type;

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_type() {
        return card_type;
    }

    public Boolean getConfirm_on_terminal() {
        return confirm_on_terminal;
    }

    public void setConfirm_on_terminal(Boolean confirm_on_terminal) {
        this.confirm_on_terminal = confirm_on_terminal;
    }

    public void setOrig_merchant_order_no(String orig_merchant_order_no) {
        this.orig_merchant_order_no = orig_merchant_order_no;
    }

    public String getOrig_merchant_order_no() {
        return orig_merchant_order_no;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public String getDescription() {
        return description;
    }

    public String getAttach() {
        return attach;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getPay_method_id() {
        return pay_method_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getMerchant_order_no() {
        return merchant_order_no;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public String getPrice_currency() {
        return price_currency;
    }

    public String getTip_amount() {
        return tip_amount;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setMerchant_order_no(String merchant_order_no) {
        this.merchant_order_no = merchant_order_no;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public void setPrice_currency(String price_currency) {
        this.price_currency = price_currency;
    }

    public void setTip_amount(String tip_amount) {
        this.tip_amount = tip_amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getPay_scenario() {
        return pay_scenario;
    }

    public void setPay_scenario(String pay_scenario) {
        this.pay_scenario = pay_scenario;
    }

    public Boolean getOn_screen_tip() {
        return on_screen_tip;
    }

    public void setOn_screen_tip(Boolean on_screen_tip) {
        this.on_screen_tip = on_screen_tip;
    }

    public void setPay_method_id(String pay_method_id) {
        this.pay_method_id = pay_method_id;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public String getCash_amount() {
        return cash_amount;
    }

    public void setCash_amount(String cash_amount) {
        this.cash_amount = cash_amount;
    }

    public Integer getReceipt_print_mode() {
        return receipt_print_mode;
    }

    public void setReceipt_print_mode(Integer receipt_print_mode) {
        this.receipt_print_mode = receipt_print_mode;
    }

    public String getCard_network_type() {
        return card_network_type;
    }

    public void setCard_network_type(String card_network_type) {
        this.card_network_type = card_network_type;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public Boolean getRequired_terminal_authentication() {
        return required_terminal_authentication;
    }

    public void setRequired_terminal_authentication(Boolean required_terminal_authentication) {
        this.required_terminal_authentication = required_terminal_authentication;
    }

    public Boolean getOn_screen_signature() {
        return on_screen_signature;
    }

    public void setOn_screen_signature(Boolean on_screen_signature) {
        this.on_screen_signature = on_screen_signature;
    }

    public String getCashier() {
        return cashier;
    }

    public void setCashier(String cashier) {
        this.cashier = cashier;
    }

    public String getTip_adjustment_amount() {
        return tip_adjustment_amount;
    }

    public void setTip_adjustment_amount(String tip_adjustment_amount) {
        this.tip_adjustment_amount = tip_adjustment_amount;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("topic", this.topic);
        json.put("app_id", this.app_id);
        if (null != merchant_order_no) {
            json.put("merchant_order_no", this.merchant_order_no);
        }
        if (null != on_screen_tip) {
            json.put("on_screen_tip", this.on_screen_tip);
        }
        if (null != pay_scenario) {
            json.put("pay_scenario", this.pay_scenario);
        }
        if (null != confirm_on_terminal) {
            json.put("confirm_on_terminal", this.confirm_on_terminal);
        }
        if (null != orig_merchant_order_no) {
            json.put("orig_merchant_order_no", this.orig_merchant_order_no);
        }
        if (null != order_amount) {
            json.put("order_amount", this.order_amount);
        }
        if (null != tip_amount) {
            json.put("tip_amount", this.tip_amount);
        }
        if (null != cash_amount) {
            json.put("cash_amount", this.cash_amount);
        }
        if (null != trans_type) {
            json.put("trans_type", this.trans_type);
        }
        if (null != description) {
            json.put("description", this.description);
        }
        if (null != notify_url) {
            json.put("notify_url", this.notify_url);
        }
        if (null != receipt_print_mode) {
            json.put("receipt_print_mode", this.receipt_print_mode);
        }
        if (null != attach) {
            json.put("attach", this.attach);
        }
        if (null != card_network_type) {
            json.put("card_network_type", this.card_network_type);
        }
        if (null != expires) {
            json.put("expires", this.expires);
        }
        if (null != card_network_type) {
            json.put("attach", this.card_network_type);
        }
        if (null != required_terminal_authentication) {
            json.put("required_terminal_authentication", this.required_terminal_authentication);
        }
        if (null != on_screen_signature) {
            json.put("on_screen_signature", this.on_screen_signature);
        }
        if (null != cashier) {
            json.put("cashier", this.cashier);
        }
        if (null != tip_adjustment_amount) {
            json.put("tip_adjustment_amount", this.tip_adjustment_amount);
        }
        return json;
    }

}

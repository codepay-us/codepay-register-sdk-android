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
     * trans type
     */
    String trans_type;
    /**
     * Specify a payment method. This field is mandatory only when "pay_scenario" is set to "SCANQR_PAY" or "BSCANQR_PAY".
     */
    String pay_method_id;
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
     * attach info
     */
    String attach;

    String msg_id;

    String pay_scenario;

    Boolean confirm_on_terminal;

    private String card_type;

    private VoiceData voice_data;

    private PrintData print_data;

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

    public void setVoice_data(VoiceData voice_data) {
        this.voice_data = voice_data;
    }

    public void setPrint_data(PrintData print_data) {
        this.print_data = print_data;
    }

    public VoiceData getVoice_data() {
        if(null == voice_data){
            voice_data = new VoiceData();
        }
        return voice_data;
    }

    public PrintData getPrint_data() {
        if(null == print_data){
            print_data = new PrintData();
        }
        return print_data;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getMsg_id() {
        return msg_id;
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

    public void setPay_method_id(String pay_method_id) {
        this.pay_method_id = pay_method_id;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("topic", this.topic);
        json.put("app_id", this.app_id);
        if (null != merchant_order_no) {
            json.put("merchant_order_no", this.merchant_order_no);
            json.put("confirm_on_terminal", false);
            json.put("pay_scenario", "SWIPE_CARD");
        }
        if (null != order_amount) {
            json.put("order_amount", this.order_amount);
        }
        if (null != tip_amount) {
            json.put("tip_amount", this.tip_amount);
        }
        json.put("trans_type", this.trans_type);
        json.put("pay_method_category", this.pay_method_id);
        if (null != description) {
            json.put("tip_amount", this.tip_amount);
        }
        if (null != notify_url) {
            json.put("notify_url", this.notify_url);
        }
        if (null != notify_url) {
            json.put("notify_url", this.notify_url);
        }
        if (null != attach) {
            json.put("attach", this.attach);
        }
        if (null != msg_id) {
            json.put("msg_id", this.msg_id);
        }
        return json;
    }

   public class VoiceData {
        /**
         * 语音播报内容
         */
        private String content;
        /**
         * 语音播报语种
         */
        private String content_locale;
        /**
         * 语音文件地址
         */
        private String content_url;

        public void setContent(String content) {
            this.content = content;
        }

        public void setContent_locale(String content_locale) {
            this.content_locale = content_locale;
        }

        public void setContent_url(String content_url) {
            this.content_url = content_url;
        }

        public String getContent() {
            return content;
        }

        public String getContent_locale() {
            return content_locale;
        }

        public String getContent_url() {
            return content_url;
        }
    }

    public class PrintData {
        /**
         * 打印文本内容
         */
        String content;
        /**
         * 打印内容文本地址
         */
        String content_url;

        public void setContent_url(String content_url) {
            this.content_url = content_url;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent_url() {
            return content_url;
        }

        public String getContent() {
            return content;
        }
    }
}

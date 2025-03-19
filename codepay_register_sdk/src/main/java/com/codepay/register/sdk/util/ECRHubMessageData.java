package com.codepay.register.sdk.util;

import org.json.JSONObject;

public class ECRHubMessageData {
    //消息主题
    public final static String ECR_HUB_TOPIC_PAIR = "ecrhub.pair";
    public final static String ECR_HUB_TOPIC_INIT = "ecrhub.pay.init";
    public final static String ECR_HUB_TOPIC_UNPAIR = "ecrhub.unpair";
    public final static String ECR_HUB_TOPIC_TRANS_PAY = "cashier.hub.trans.pay";
    public final static String ECR_HUB_TOPIC_TRANS_REFUND = "cashier.hub.trans.refund";
    public final static String ECR_HUB_TOPIC_TRANS_QUERY = "cashier.hub.trans.query";
    public final static String ECR_HUB_TOPIC_TRANS_CLOSE_ORDER = "cashier.hub.trans.close";
    public final static String ECR_HUB_TOPIC_CLOUD_PAY = "cloud.hub.pay.order";

    public final static String ECR_HUB_TOPIC_WLAN_PAY = "ecrhub.pay.order";

    public final static String ECR_HUB_TOPIC_WLAN_QUERY = "ecrhub.pay.query";

    public final static String ECR_HUB_TOPIC_WLAN_INIT = "ecrhub.init";

    public final static String ECR_HUB_TOPIC_WLAN_HEART_BEAT = "ecrhub.heartbeat";
    public final static String ECR_HUB_TOPIC_WLAN_CLOSE = "ecrhub.pay.close";
    public final static String ECR_HUB_TOPIC_CLOUD_TRANS_ORDER = "cloud.hub.trans.order";

    public final static String ECR_HUB_TOPIC_TRANS_SUBMIT = "cashier.hub.trans.submit";
    public final static String ECR_HUB_TOPIC_CLOUD_REFUND = "cloud.hub.refund.order";
    public final static String ECR_HUB_TOPIC_CLOUD_CLOSE_ORDER = "cloud.hub.close.order";

    public final static String ECR_HUB_TOPIC_CLOUD_NOTIFY = "cloud.hub.trans.notify";

    public final static String ECR_HUB_TOPIC_TOPIC_NOTIFY = "cashier.hub.trans.notify";

    public final static String ORDER_QUEUE_MODE_FIFO = "FIFO";

    public final static String ORDER_QUEUE_MODE_FILO = "FILO";

    /**
     * 银行卡支付方式
     */
    public final static String ECR_HUB_BANKCARD_PAY_TYPE = "BANKCARD";
    /**
     * 扫码主扫
     */
    public final static String ECR_HUB_QR_C_SCAN_B_PAY_TYPE = "QR_C_SCAN_B";
    /**
     * 扫码被扫
     */
    public final static String ECR_HUB_QR_B_SCAN_C = "QR_B_SCAN_C";


    public boolean isCloseOrder() {
        return topic.equals(ECR_HUB_TOPIC_CLOUD_CLOSE_ORDER) || topic.equals(ECR_HUB_TOPIC_TRANS_CLOSE_ORDER) || topic.equals(ECR_HUB_TOPIC_WLAN_CLOSE);
    }

    public boolean isNotificationOrder() {
        return topic.equals(ECR_HUB_TOPIC_CLOUD_NOTIFY) || topic.equals(ECR_HUB_TOPIC_TOPIC_NOTIFY);
    }

    /**
     * 消息主题
     */
    private String topic = "";
    /**
     * 应用id
     */
    private String app_id;

    /**
     * 消息id
     */
    private String request_id;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 交易是否成功
     */
    private String response_code;

    private String response_msg;

    private BizData biz_data;

    private DeviceData device_data;

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_msg(String response_msg) {
        this.response_msg = response_msg;
    }

    public String getResponse_msg() {
        return response_msg;
    }

    public void setBiz_data(BizData biz_data) {
        this.biz_data = biz_data;
    }

    public void setDevice_data(DeviceData device_data) {
        this.device_data = device_data;
    }

    public BizData getBiz_data() {
        if (null == biz_data) {
            biz_data = new BizData();
        }
        return biz_data;
    }

    public DeviceData getDevice_data() {
        if (null == device_data) {
            device_data = new DeviceData();
        }
        return device_data;
    }

    public void setCall_app_mode(String call_app_mode) {
        this.call_app_mode = call_app_mode;
    }

    public String getCall_app_mode() {
        return call_app_mode;
    }

    private String call_app_mode;

    public void setCallAppMode(String callAppMode) {
        this.call_app_mode = callAppMode;
    }

    public String getCallAppMode() {
        return call_app_mode;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public class DeviceData {
        /**
         * device mac address
         */
        private String mac_address = "";
        /**
         * device name
         */
        private String device_name = "";
        /**
         * device alias name
         */
        private String alias_name = "";
        /**
         * server ip address
         */
        private String ip_address = "";
        /**
         * server port number
         */
        private String port = "";

        public void setAlias_name(String alias_name) {
            this.alias_name = alias_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public void setIp_address(String ip_address) {
            this.ip_address = ip_address;
        }

        public void setMac_address(String mac_address) {
            this.mac_address = mac_address;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getAlias_name() {
            return alias_name;
        }

        public String getDevice_name() {
            return device_name;
        }

        public String getIp_address() {
            return ip_address;
        }

        public String getMac_address() {
            return mac_address;
        }

        public String getPort() {
            return port;
        }
    }

    public class BizData {
        /**
         * 是否需要终端确认
         */
        private boolean confirm_on_terminal;

        private boolean on_screen_tip;
        /**
         * 多订单处理模式
         */
        private String order_queue_mode;

        /**
         * 时间有效期
         */
        private int expires;

        /**
         * 交易号
         */
        private String trans_no;

        /**
         * 调用方订单号
         */
        private String merchant_order_no;

        /**
         * 原调用方订单号
         */
        private String orig_merchant_order_no;

        /**
         * 支付方式类目
         */
        private String pay_scenario;

        /**
         * 支付方式id
         */
        private String pay_method_id;

        /**
         * 附加数据
         */
        private String attach;

        /**
         * 描述信息
         */
        private String description;

        /**
         * 回调地址
         */
        private String notify_url;

        /**
         * 交易状态
         */
        private String trans_status;

        /**
         * 错误信息
         */
        private String msg;

        private String push_no;

        private String order_amount;

        private String cashback_amount;

        private String tip_amount;

        private String tax_amount;

        private String price_currency;

        private String trans_type;

        private boolean limit_length;

        private boolean is_auto_settlement;

        private Integer receipt_print_mode;

        private String card_type;

        private String card_network_type;

        private boolean required_terminal_authentication;

        private boolean on_screen_signature;

        private String trans_end_time;

        private String cashier;

        private String auth_code;

        private String ref_no;

        private String card_no;

        private String entry_mode;

        private String merchant_no;

        private String merchant_name;

        private String pay_channel_merchant_id;

        private String pay_channel_terminal_id;

        private String tip_adjustment_amount;


        public void setTax_amount(String tax_amount) {
            this.tax_amount = tax_amount;
        }

        public String getTax_amount() {
            return tax_amount;
        }

        public void setCard_type(String card_type) {
            this.card_type = card_type;
        }

        public String getCard_type() {
            return card_type;
        }

        public boolean isIs_auto_settlement() {
            return is_auto_settlement;
        }

        public boolean isConfirm_on_terminal() {
            return confirm_on_terminal;
        }

        public String getPay_scenario() {
            return pay_scenario;
        }

        public void setPay_scenario(String pay_scenario) {
            this.pay_scenario = pay_scenario;
        }

        public void setIs_auto_settlement(boolean is_auto_settlement) {
            this.is_auto_settlement = is_auto_settlement;
        }

        String token;

        public void setCashback_amount(String cashback_amount) {
            this.cashback_amount = cashback_amount;
        }

        public String getCashback_amount() {
            return cashback_amount;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setLimit_length(boolean limit_length) {
            this.limit_length = limit_length;
        }

        public boolean getLimit_length() {
            return limit_length;
        }

        public void setPush_no(String push_no) {
            this.push_no = push_no;
        }

        public String getPush_no() {
            return push_no;
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

        public String getTrans_type() {
            return trans_type;
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

        public void setTrans_type(String trans_type) {
            this.trans_type = trans_type;
        }

        public String getTrans_status() {
            return trans_status;
        }

        public int getExpires() {
            return expires;
        }

        public String getOrder_queue_mode() {
            return order_queue_mode;
        }

        public void setTrans_status(String trans_status) {
            this.trans_status = trans_status;
        }

        public void setAttach(String attach) {
            this.attach = attach;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setMerchant_order_no(String merchant_order_no) {
            this.merchant_order_no = merchant_order_no;
        }

        public void setNotify_url(String notify_url) {
            this.notify_url = notify_url;
        }

        public void setOrig_merchant_order_no(String orig_merchant_order_no) {
            this.orig_merchant_order_no = orig_merchant_order_no;
        }

        public void setPay_method_id(String pay_method_id) {
            this.pay_method_id = pay_method_id;
        }

        public void setTrans_no(String trans_no) {
            this.trans_no = trans_no;
        }

        public String getAttach() {
            return attach;
        }

        public String getDescription() {
            return description;
        }

        public String getMerchant_order_no() {
            return merchant_order_no;
        }

        public String getNotify_url() {
            return notify_url;
        }

        public String getOrig_merchant_order_no() {
            return orig_merchant_order_no;
        }

        public String getPay_method_id() {
            return pay_method_id;
        }

        public String getEndAmount() {
            try {
                int amount = Integer.parseInt(order_amount);
                if (null != order_amount) {
                    if (null != tip_amount) {
                        amount += Integer.parseInt(tip_amount);
                    }
                    if (null != cashback_amount) {
                        amount += Integer.parseInt(cashback_amount);
                    }
                }
                return "" + amount;
            } catch (Exception e) {
                return order_amount;
            }
        }

        public String getTrans_no() {
            return trans_no;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        /**
         * 获取Cashier识别应用间调用的支付方式
         */
        public String getCashierInvokePayType(String type) {
            switch (type) {
                case ECR_HUB_BANKCARD_PAY_TYPE:
                    return "CARD";
                case ECR_HUB_QR_C_SCAN_B_PAY_TYPE:
                    return "SCANQR";
                case ECR_HUB_QR_B_SCAN_C:
                    return "BSCANQR";
            }
            return "CARD";
        }

        public void setConfirm_on_terminal(boolean confirm_on_terminal) {
            this.confirm_on_terminal = confirm_on_terminal;
        }

        public boolean getConfirm_on_terminal() {
            return confirm_on_terminal;
        }

        public Integer getReceipt_print_mode() {
            return receipt_print_mode;
        }

        public void setReceipt_print_mode(Integer receipt_print_mode) {
            this.receipt_print_mode = receipt_print_mode;
        }

        public void setExpires(int expires) {
            this.expires = expires;
        }

        public void setOrder_queue_mode(String order_queue_mode) {
            this.order_queue_mode = order_queue_mode;
        }

        public boolean isOn_screen_tip() {
            return on_screen_tip;
        }

        public void setOn_screen_tip(boolean on_screen_tip) {
            this.on_screen_tip = on_screen_tip;
        }

        public String getCard_network_type() {
            return card_network_type;
        }

        public void setCard_network_type(String card_network_type) {
            this.card_network_type = card_network_type;
        }

        public boolean isRequired_terminal_authentication() {
            return required_terminal_authentication;
        }

        public void setRequired_terminal_authentication(boolean required_terminal_authentication) {
            this.required_terminal_authentication = required_terminal_authentication;
        }

        public boolean isOn_screen_signature() {
            return on_screen_signature;
        }

        public void setOn_screen_signature(boolean on_screen_signature) {
            this.on_screen_signature = on_screen_signature;
        }

        public String getTrans_end_time() {
            return trans_end_time;
        }

        public void setTrans_end_time(String trans_end_time) {
            this.trans_end_time = trans_end_time;
        }

        public String getCashier() {
            return cashier;
        }

        public void setCashier(String cashier) {
            this.cashier = cashier;
        }

        public String getAuth_code() {
            return auth_code;
        }

        public void setAuth_code(String auth_code) {
            this.auth_code = auth_code;
        }

        public String getRef_no() {
            return ref_no;
        }

        public void setRef_no(String ref_no) {
            this.ref_no = ref_no;
        }

        public String getCard_no() {
            return card_no;
        }

        public void setCard_no(String card_no) {
            this.card_no = card_no;
        }

        public String getEntry_mode() {
            return entry_mode;
        }

        public void setEntry_mode(String entry_mode) {
            this.entry_mode = entry_mode;
        }

        public String getMerchant_no() {
            return merchant_no;
        }

        public void setMerchant_no(String merchant_no) {
            this.merchant_no = merchant_no;
        }

        public String getMerchant_name() {
            return merchant_name;
        }

        public void setMerchant_name(String merchant_name) {
            this.merchant_name = merchant_name;
        }

        public String getPay_channel_merchant_id() {
            return pay_channel_merchant_id;
        }

        public void setPay_channel_merchant_id(String pay_channel_merchant_id) {
            this.pay_channel_merchant_id = pay_channel_merchant_id;
        }

        public String getPay_channel_terminal_id() {
            return pay_channel_terminal_id;
        }

        public void setPay_channel_terminal_id(String pay_channel_terminal_id) {
            this.pay_channel_terminal_id = pay_channel_terminal_id;
        }

        public String getTip_adjustment_amount() {
            return tip_adjustment_amount;
        }

        public void setTip_adjustment_amount(String tip_adjustment_amount) {
            this.tip_adjustment_amount = tip_adjustment_amount;
        }
    }

}
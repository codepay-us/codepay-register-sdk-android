package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.payment.PaymentRequestParams
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import com.codepay.register.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_refund.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class RefundActivity : Activity() {

    fun getCurDateStr(format: String?): String? {
        val c = Calendar.getInstance()
        return date2Str(c, format)
    }

    fun date2Str(c: Calendar?, format: String?): String? {
        return if (c == null) null else date2Str(
            c.time,
            format
        )
    }

    fun date2Str(d: Date?, format: String?): String? {
        var format = format
        return if (d == null) {
            null
        } else {
            if (format == null || format.length == 0) {
                format = "yyyy-MM-dd HH:mm:ss"
            }
            val sdf = SimpleDateFormat(format)
            sdf.format(d)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val merchantOrderNo = edit_input_merchant_order_no.text.toString()
            if (merchantOrderNo.isEmpty()) {
                Toast.makeText(this, "Please input merchant order no", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val params =
                PaymentRequestParams()
            params.trans_type = Constants.TRANS_TYPE_REFUND
            params.app_id = "wz6012822ca2f1as78"
            params.orig_merchant_order_no = merchantOrderNo
            params.merchant_order_no = "123" + getCurDateStr("yyyyMMddHHmmss")
            params.pay_method_id = "BANKCARD"
            params.order_amount = amount
            params.msg_id = "111111"
            val voiceData = params.voice_data
            voiceData.content = "CodePay Register Received a new order"
            voiceData.content_locale = "en-US"
            params.voice_data = voiceData
            runOnUiThread {
                tv_btn_3.text =
                    "Send data" + params.toJSON().toString()
            }
            MainActivity.mClient.payment.refund(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "交易失败" + errorMsg
                    }
                }

                override fun onSuccess(data: PaymentResponseParams?) {
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "Result:" + JSON.toJSON(data)
                    }
                }
            })
        }

        tv_btn_4.setOnClickListener {
            val merchantOrderNo = edit_input_merchant_order_no.text.toString()
            if (merchantOrderNo.isEmpty()) {
                Toast.makeText(this, "Please input merchant order no", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val params = PaymentParams()
            params.transType = Constants.TRANS_TYPE_VOID
            params.appId = "wz6012822ca2f1as78"
            params.origMerchantOrderNo = merchantOrderNo
            params.merchantOrderNo = "123" + getCurDateStr("yyyyMMddHHmmss")
            params.payMethod = "BANKCARD"
            params.msgId = "111111"
            val voiceData = params.voice_data
            voiceData.content = "CodePay Register Received a new order"
            voiceData.content_locale = "en-US"
            params.voice_data = voiceData
            runOnUiThread {
                tv_btn_3.text =
                    "Send data" + params.toJSON().toString()
            }
            MainActivity.mClient.payment.cancel(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "交易失败" + errorMsg
                    }
                }

                override fun onSuccess(data: String?) {
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "Result:" + data.toString()
                    }
                }
            })
        }
    }
}
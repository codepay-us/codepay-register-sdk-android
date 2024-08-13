package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.payment.PaymentRequestParams
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_auth_complete.*
import java.text.SimpleDateFormat
import java.util.*

class AuthCompleteActivity : Activity() {
    companion object {
        lateinit var mClient: ECRHubClient
    }

    var merchantOrderNo: String? = null
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
        setContentView(R.layout.activity_auth_complete)
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            val orderNo = edit_input_merchant_order_no.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val params = PaymentRequestParams()
            params.app_id = "wz6012822ca2f1as78"
            merchantOrderNo = "123" + getCurDateStr("yyyyMMddHHmmss")
            params.merchant_order_no = merchantOrderNo
            if (orderNo.isEmpty()) {
                if (sharedPreferences.getString("merchant_order_no", "").toString().isEmpty()) {
                    Toast.makeText(this, "Please input orig merchant order no", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                } else {
                    params.orig_merchant_order_no =
                        sharedPreferences.getString("merchant_order_no", "").toString()
                }
            } else {
                params.orig_merchant_order_no = orderNo
            }
            params.order_amount = amount
            params.confirm_on_terminal = false
            params.pay_scenario = "SWIPE_CARD"
            val voiceData = params.voice_data
            voiceData.content = "CodePay Register Received a new order"
            voiceData.content_locale = "en-US"
            params.voice_data = voiceData
            runOnUiThread {
                tv_btn_3.text =
                    "Send Complete data --> " + params.toJSON().toString()
            }
            mClient.payment.completion(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "Failure:" + errorMsg
                    }
                }

                override fun onSuccess(data: PaymentResponseParams?) {
                    val editor = sharedPreferences.edit()
                    editor.putString("merchant_order_no", params.merchant_order_no)
                    editor.apply()
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "Result:" + JSON.toJSON(data)
                                .toString()
                    }
                }
            })
        }
        tv_btn_close.setOnClickListener {
            val merchantOrderNo = sharedPreferences.getString("merchant_order_no", "").toString()
            val params =
                PaymentRequestParams()
            if (merchantOrderNo.isEmpty()) {
                Toast.makeText(this, "Transaction does not exist", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                params.merchant_order_no = merchantOrderNo
            }
            params.app_id = "wz6012822ca2f1as78"
            runOnUiThread {
                tv_btn_3.text =
                    "Send Close data --> " + params.toJSON().toString()
            }
            mClient.payment.close(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "Failure:" + errorMsg
                    }
                }

                override fun onSuccess(data: PaymentResponseParams?) {
                    val editor = sharedPreferences.edit()
                    editor.remove("merchant_order_no")
                    editor.apply()
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "result:" + JSON.toJSON(data)
                    }
                }

            })
        }
    }
}
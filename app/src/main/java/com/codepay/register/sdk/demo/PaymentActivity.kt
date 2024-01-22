package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.codepay.register.sdk.client.payment.PaymentParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import com.codepay.register.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_payment.edit_input_amount
import kotlinx.android.synthetic.main.activity_payment.tv_btn_1
import kotlinx.android.synthetic.main.activity_payment.tv_btn_2
import kotlinx.android.synthetic.main.activity_payment.tv_btn_3
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class PaymentActivity : Activity() {
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
        setContentView(R.layout.activity_payment)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val params = PaymentParams()
            params.transType = Constants.TRANS_TYPE_PURCHASE
            params.appId = "wz6012822ca2f1as78"
            merchantOrderNo = "123" + getCurDateStr("yyyyMMddHHmmss")
            params.merchantOrderNo = merchantOrderNo
            params.transAmount = amount
            params.msgId = "111111"
            val voiceData = params.voice_data
            voiceData.content = "AddpayCashier2 Received a new order"
            voiceData.content_locale = "en-US"
            params.voice_data = voiceData
            runOnUiThread {
                tv_btn_3.text =
                    tv_btn_3.text.toString() + "\n" + "Send data:" + params.toJSON().toString()
            }
            MainActivity.mClient.payment.purchase(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "Failure:" + errorMsg
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
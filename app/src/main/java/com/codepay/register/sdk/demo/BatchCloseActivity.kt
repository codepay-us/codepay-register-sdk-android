package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.payment.PaymentRequestParams
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_batch_close.tv_btn_1
import kotlinx.android.synthetic.main.activity_batch_close.tv_btn_2
import kotlinx.android.synthetic.main.activity_batch_close.tv_btn_3
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class BatchCloseActivity : Activity() {
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
        setContentView(R.layout.activity_batch_close)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val params = PaymentRequestParams()
            params.app_id = "wz2b6cef2f18008ee7"
            merchantOrderNo = "123" + getCurDateStr("yyyyMMddHHmmss")
            params.merchant_order_no = merchantOrderNo
            runOnUiThread {
                tv_btn_3.text =
                    "Send Batch Close data --> " + params.toJSON().toString()
            }
            mClient.payment.batchClose(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "Failure:" + errorMsg
                    }
                }

                override fun onSuccess(data: PaymentResponseParams?) {
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "Result:" + JSON.toJSON(data)
                                .toString()
                    }
                }
            })
        }
    }
}
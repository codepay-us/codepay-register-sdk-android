package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.payment.PaymentRequestParams
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_payment.edit_input_amount
import kotlinx.android.synthetic.main.activity_payment.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class PaymentActivity : Activity() {
    companion object {
        lateinit var mClient: ECRHubClient
    }
    private var merchantOrderNo: String? = null
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
            if (format.isNullOrEmpty()) {
                format = "yyyy-MM-dd HH:mm:ss"
            }
            val sdf = SimpleDateFormat(format)
            sdf.format(d)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_amount.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val params = PaymentRequestParams()
            params.app_id = "wz2b6cef2f18008ee7"
            merchantOrderNo = "123" + getCurDateStr("yyyyMMddHHmmss")
            params.merchant_order_no = merchantOrderNo
            params.order_amount = amount
            params.on_screen_tip = false
            params.receipt_print_mode = 0
            params.pay_scenario = "SWIPE_CARD"
            runOnUiThread {
                tv_btn_3.text =
                    "Send Sale data --> ${params.toJSON()}"
            }
            mClient.payment.sale(params, object :
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
                            tv_btn_3.text.toString() + "\n" + "Result:" + JSON.toJSON(data).toString()
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
            params.app_id = "wz2b6cef2f18008ee7"
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
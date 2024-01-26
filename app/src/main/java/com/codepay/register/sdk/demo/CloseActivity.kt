package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.payment.PaymentRequestParams
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import kotlinx.android.synthetic.main.activity_close.edit_input_merchant_order_no
import kotlinx.android.synthetic.main.activity_close.tv_btn_1
import kotlinx.android.synthetic.main.activity_close.tv_btn_2
import kotlinx.android.synthetic.main.activity_close.tv_btn_3
import kotlinx.android.synthetic.main.activity_payment.*

class CloseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_close)
        val sharedPreferences = getSharedPreferences(packageName, MODE_PRIVATE)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val merchantOrderNo = edit_input_merchant_order_no.text.toString()
            val params =
                PaymentRequestParams()
            if (merchantOrderNo.isEmpty()){
                params.orig_merchant_order_no = sharedPreferences.getString("merchant_order_no","").toString()
            } else {
                params.orig_merchant_order_no = merchantOrderNo
            }
            params.app_id = "wz6012822ca2f1as78"
            params.msg_id = "11322"
            runOnUiThread {
                tv_btn_3.text =
                    "Send data --> " + params.toJSON().toString()
            }
            MainActivity.mClient.payment.close(params, object :
                ECRHubResponseCallBack {
                override fun onError(errorCode: String?, errorMsg: String?) {
                    runOnUiThread {
                        tv_btn_3.text = tv_btn_3.text.toString() + "\n" + "Failure:" + errorMsg
                    }
                }

                override fun onSuccess(data: PaymentResponseParams?) {
                    runOnUiThread {
                        tv_btn_3.text =
                            tv_btn_3.text.toString() + "\n" + "result:" + JSON.toJSON(data)
                    }
                }

            })
        }

    }
}
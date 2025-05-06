package com.codepay.register.sdk.demo

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.payment.PaymentRequestParams
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import com.codepay.register.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_tip_adjustment.edit_input_merchant_order_no
import kotlinx.android.synthetic.main.activity_tip_adjustment.edit_input_tip_adjust_amount
import kotlinx.android.synthetic.main.activity_tip_adjustment.tv_btn_1
import kotlinx.android.synthetic.main.activity_tip_adjustment.tv_btn_2
import kotlinx.android.synthetic.main.activity_tip_adjustment.tv_btn_3

class TipAdjustmentActivity : Activity() {
    companion object {
        lateinit var mClient: ECRHubClient
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_adjustment)
        tv_btn_2.setOnClickListener {
            finish()
        }
        tv_btn_1.setOnClickListener {
            val amount = edit_input_tip_adjust_amount.text.toString()
            if (amount.isEmpty()) {
                Toast.makeText(this, "Please input amount", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val merchantOrderNo = edit_input_merchant_order_no.text.toString()
            val params = PaymentRequestParams()
            params.app_id = "wz2b6cef2f18008ee7"
            if (merchantOrderNo.isEmpty()) {
                Toast.makeText(this, "Please input orig merchant order no", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            } else {
                params.merchant_order_no = merchantOrderNo

            }
            params.tip_adjustment_amount = amount
            runOnUiThread {
                tv_btn_3.text =
                    "Send TipAdjustment data" + params.toJSON().toString()
            }
            mClient.payment.tipAdjustment(params, object :
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
    }
}
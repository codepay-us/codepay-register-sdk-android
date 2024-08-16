package com.codepay.register.sdk.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.ECRHubConfig
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.listener.ECRHubConnectListener
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import com.codepay.register.sdk.util.Constants
import kotlinx.android.synthetic.main.activity_usb.tv_btn_open
import kotlinx.android.synthetic.main.activity_usb.ll_layout1
import kotlinx.android.synthetic.main.activity_usb.tv_btn_auth
import kotlinx.android.synthetic.main.activity_usb.tv_btn_cashback
import kotlinx.android.synthetic.main.activity_usb.tv_btn_close
import kotlinx.android.synthetic.main.activity_usb.tv_btn_close_order
import kotlinx.android.synthetic.main.activity_usb.tv_btn_complete
import kotlinx.android.synthetic.main.activity_usb.tv_btn_init
import kotlinx.android.synthetic.main.activity_usb.tv_btn_query
import kotlinx.android.synthetic.main.activity_usb.tv_btn_refund
import kotlinx.android.synthetic.main.activity_usb.tv_btn_sale

class UsbActivity : Activity(), ECRHubConnectListener, View.OnClickListener {
    companion object {
        lateinit var mClient: ECRHubClient
    }

    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usb)
        val config = ECRHubConfig()
        mClient = ECRHubClient.getInstance()
        mClient.init(config, this, this, Constants.ECRHubType.USB)
        tv_btn_open.setOnClickListener(this)
        tv_btn_close.setOnClickListener(this)
        tv_btn_sale.setOnClickListener(this)
        tv_btn_refund.setOnClickListener(this)
        tv_btn_auth.setOnClickListener(this)
        tv_btn_complete.setOnClickListener(this)
        tv_btn_cashback.setOnClickListener(this)
        tv_btn_query.setOnClickListener(this)
        tv_btn_close_order.setOnClickListener(this)
        tv_btn_init.setOnClickListener(this)
    }

    override fun onConnect() {
        Log.e("Test", "onConnect")
        runOnUiThread {
            ll_layout1.visibility = View.VISIBLE
            tv_btn_open.visibility = View.GONE
            tv_btn_close.visibility = View.VISIBLE
            Toast.makeText(this, "Connect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onDisconnect() {
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            tv_btn_open.visibility = View.VISIBLE
            tv_btn_close.visibility = View.GONE
            Toast.makeText(this, "Disconnect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onError(errorCode: String?, errorMsg: String?) {
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            tv_btn_open.visibility = View.VISIBLE
            tv_btn_close.visibility = View.GONE
            Toast.makeText(this, "Connect Fail!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.tv_btn_init -> {
                mClient.payment.init(object : ECRHubResponseCallBack {
                    override fun onError(errorCode: String?, errorMsg: String?) {
                        runOnUiThread {
                            Toast.makeText(applicationContext, "init error", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onSuccess(data: PaymentResponseParams?) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "init success:" + JSON.toJSON(data).toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                })
            }

            R.id.tv_btn_open -> {
                mClient.connect()
            }


            R.id.tv_btn_close -> {
                mClient.disConnect()
            }

            R.id.tv_btn_sale -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                PaymentActivity.mClient = mClient
                startActivity(Intent(applicationContext, PaymentActivity::class.java))
            }

            R.id.tv_btn_refund -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                RefundActivity.mClient = mClient
                startActivity(Intent(applicationContext, RefundActivity::class.java))
            }

            R.id.tv_btn_auth -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                AuthActivity.mClient = mClient
                startActivity(Intent(applicationContext, AuthActivity::class.java))
            }

            R.id.tv_btn_complete -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                AuthCompleteActivity.mClient = mClient
                startActivity(Intent(applicationContext, AuthCompleteActivity::class.java))
            }

            R.id.tv_btn_cashback -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                CashBackActivity.mClient = mClient
                startActivity(Intent(applicationContext, CashBackActivity::class.java))
            }

            R.id.tv_btn_query -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                QueryActivity.mClient = mClient
                startActivity(Intent(applicationContext, QueryActivity::class.java))
            }

            R.id.tv_btn_close_order -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                CloseActivity.mClient = mClient
                startActivity(Intent(applicationContext, CloseActivity::class.java))
            }
        }
    }
}
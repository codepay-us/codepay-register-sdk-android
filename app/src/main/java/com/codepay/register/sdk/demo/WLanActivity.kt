package com.codepay.register.sdk.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.codepay.register.sdk.client.ECRHubClient
import com.codepay.register.sdk.client.ECRHubConfig
import com.codepay.register.sdk.client.payment.PaymentResponseParams
import com.codepay.register.sdk.device.ECRHubDevice
import com.codepay.register.sdk.device.ECRHubWebSocketDiscoveryService
import com.codepay.register.sdk.listener.ECRHubConnectListener
import com.codepay.register.sdk.listener.ECRHubPairListener
import com.codepay.register.sdk.listener.ECRHubResponseCallBack
import com.codepay.register.sdk.util.Constants
import com.codepay.register.sdk.util.ECRHubMessageData
import kotlinx.android.synthetic.main.activity_wlan.*

class WLanActivity : Activity(), ECRHubConnectListener, OnClickListener, ECRHubPairListener {
    companion object {
        lateinit var mClient: ECRHubClient
    }

    private var mPairServer: ECRHubWebSocketDiscoveryService? = null

    private var mPairedList = mutableListOf<ECRHubDevice>()

    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wlan)


        val config = ECRHubConfig()
        mClient = ECRHubClient.getInstance()
        mClient.init(config, this, this, Constants.ECRHubType.WLAN)
        mPairServer = ECRHubWebSocketDiscoveryService(this)
        mPairedList = mPairServer!!.pairedDeviceList
        tv_btn_start.setOnClickListener(this)
        tv_btn_connect.setOnClickListener(this)
        tv_btn_disconnect.setOnClickListener(this)
        tv_btn_unpair.setOnClickListener(this)
        tv_btn_sale.setOnClickListener(this)
        tv_btn_refund.setOnClickListener(this)
        tv_btn_auth.setOnClickListener(this)
        tv_btn_complete.setOnClickListener(this)
        tv_btn_cashback.setOnClickListener(this)
        tv_btn_query.setOnClickListener(this)
        tv_btn_close.setOnClickListener(this)
        tv_btn_exit.setOnClickListener(this)
    }

    override fun onConnect() {
        Log.e("Test", "onConnect")
        runOnUiThread {
            ll_layout1.visibility = View.VISIBLE
            Toast.makeText(this, "Connect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = true
    }

    override fun onDisconnect() {
        Log.e("Test", "onDisconnect")
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            Toast.makeText(this, "Disconnect Success!", Toast.LENGTH_LONG).show()
        }
        isConnected = false
    }

    override fun onError(errorCode: String?, errorMsg: String?) {
        Log.e("Test", "onError")
        runOnUiThread {
            ll_layout1.visibility = View.GONE
            Toast.makeText(this, "Connect Error:$errorMsg", Toast.LENGTH_LONG).show()
        }
        isConnected = false
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_btn_start -> {
                // Get the connectivity manager
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                // Check if the network is connected and is of type WI-FI
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    mPairServer?.start(this@WLanActivity)
                    runOnUiThread {
                        Toast.makeText(this, "Start Server", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // If not connected to WI-FI, display a prompt
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Please connect to WI-FI before performing this operation",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            R.id.tv_btn_connect -> {
                // Get the connectivity manager
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                // Check if the network is connected and is of type WI-FI
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    mPairedList = mPairServer!!.pairedDeviceList
                    for (device in mPairedList) {
                        Log.e("terminal_sn", device.terminal_sn)
                        Log.e("ws_address", device.ws_address)
                    }
                    if (mPairedList.isEmpty()) {
                        runOnUiThread {
                            Toast.makeText(this, "Paired list is empty", Toast.LENGTH_LONG).show()
                        }
                        return
                    }
                    if (isConnected) {
                        runOnUiThread {
                            Toast.makeText(this, "Server is connected", Toast.LENGTH_LONG).show()
                        }
                        return
                    }
                    // Perform the connection operation
                    val ip = "ws://" + mPairedList[0].ip_address + ":" + mPairedList[0].port
                    ECRHubClient.getInstance().connect(ip)
                    runOnUiThread {
                        tv_text_1.text = "connect $ip"
                    }
                } else {
                    // If not connected to WI-FI, display a prompt
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Please connect to WI-FI before performing this operation",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }


            R.id.tv_btn_disconnect -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                mClient.disConnect()
                runOnUiThread {
                    tv_text_1.text = ""
                }
            }

            R.id.tv_btn_unpair -> {
                mPairedList = mPairServer!!.pairedDeviceList
                println("mPairedList: $mPairedList")
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                } else {
                    mPairServer?.unPair(mPairedList[0], object : ECRHubResponseCallBack {
                        override fun onError(errorCode: String?, errorMsg: String?) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WLanActivity,
                                    "unPair failure:$errorMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onSuccess(data: PaymentResponseParams?) {
                            mClient.disConnect()
                            runOnUiThread {
                                ll_layout1.visibility = View.GONE
                                Toast.makeText(
                                    this@WLanActivity,
                                    "Unpair Success!",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }

                    })
                }
//                if (mPairedList.isNotEmpty()) {

//                } else {
//                    runOnUiThread {
//                        Toast.makeText(this, "Paired list is empty", Toast.LENGTH_LONG).show()
//                    }
//                }
            }

            R.id.tv_btn_exit -> {
                runOnUiThread {
                    tv_text_1.text = "The APP is exiting..."
                }
                mPairServer?.stop()
                mClient.disConnect()
                finish()
            }

            R.id.tv_btn_sale -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                PaymentActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, PaymentActivity::class.java))
            }

            R.id.tv_btn_refund -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                RefundActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, RefundActivity::class.java))
            }

            R.id.tv_btn_auth -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                AuthActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, AuthActivity::class.java))
            }

            R.id.tv_btn_complete -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                AuthCompleteActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, AuthCompleteActivity::class.java))
            }

            R.id.tv_btn_cashback -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                CashBackActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, CashBackActivity::class.java))
            }

            R.id.tv_btn_query -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                QueryActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, QueryActivity::class.java))
            }

            R.id.tv_btn_close -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                CloseActivity.mClient = UsbActivity.mClient
                startActivity(Intent(applicationContext, CloseActivity::class.java))
            }
        }
    }

    override fun onDevicePair(data: ECRHubMessageData?, ip: String?) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pair Device")
            builder.setMessage("Are you pair the " + data?.device_data?.device_name + " device?")
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Pair"
            ) { p0, _ ->
                mPairServer?.confirmPair(data)
                ECRHubClient.getInstance().connect(ip)
                p0?.dismiss()
            }
            builder.setNegativeButton("Cancel") { p0, _ ->
                mPairServer?.cancelPair(data)
                p0?.dismiss()
            }
            builder.show()
        }
    }

    override fun onDeviceUnpair(data: ECRHubMessageData?) {
        mPairedList = mPairServer!!.pairedDeviceList
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Unpair Device")
            builder.setMessage("Register unpair the " + data?.device_data?.device_name + " device")
            builder.setCancelable(false)
            println("mPairedList: $mPairedList")
            builder.setPositiveButton(
                "confirm"
            ) { p0, _ ->
                if (mPairedList.isNotEmpty()) {
                    mPairServer?.unPair(mPairedList[0], object : ECRHubResponseCallBack {
                        override fun onError(errorCode: String?, errorMsg: String?) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@WLanActivity,
                                    "unPair failure:$errorMsg",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onSuccess(data: PaymentResponseParams?) {
                            mClient.disConnect()
                            runOnUiThread {
                                ll_layout1.visibility = View.GONE
                                Toast.makeText(
                                    this@WLanActivity,
                                    "Unpair Success!",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    })
                    p0?.dismiss()
                } else {
                    return@setPositiveButton
                }
            }
            builder.show()
        }
    }
}

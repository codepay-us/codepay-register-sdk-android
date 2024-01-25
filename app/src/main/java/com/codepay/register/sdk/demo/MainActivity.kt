package com.codepay.register.sdk.demo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
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
import com.codepay.register.sdk.util.ECRHubMessageData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.tv_btn_1
import kotlinx.android.synthetic.main.activity_main.tv_btn_2
import kotlinx.android.synthetic.main.activity_main.tv_btn_3
import kotlinx.android.synthetic.main.activity_payment.*

class MainActivity : Activity(), ECRHubConnectListener, OnClickListener, ECRHubPairListener {
    companion object {
        lateinit var mClient: ECRHubClient
    }

    private var mPairServer: ECRHubWebSocketDiscoveryService? = null

    private var mPairedList = mutableListOf<ECRHubDevice>()

    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config = ECRHubConfig()
        mClient = ECRHubClient.getInstance()
        mClient.init(config, this)
        mPairServer = ECRHubWebSocketDiscoveryService(this)
        mPairedList = mPairServer!!.pairedDeviceList
        tv_btn_1.setOnClickListener(this)
        tv_btn_2.setOnClickListener(this)
        tv_btn_3.setOnClickListener(this)
        tv_btn_5.setOnClickListener(this)
        tv_btn_6.setOnClickListener(this)
        tv_btn_7.setOnClickListener(this)
        tv_btn_8.setOnClickListener(this)
        tv_btn_9.setOnClickListener(this)
        tv_btn_10.setOnClickListener(this)
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
            R.id.tv_btn_1 -> {
                // Get the connectivity manager
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                // Check if the network is connected and is of type WI-FI
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    mPairServer?.start(this@MainActivity)
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

            R.id.tv_btn_2 -> {
                // Get the connectivity manager
                val connectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val network = connectivityManager.activeNetwork
                // Check if the network is connected and is of type WI-FI
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    mPairedList = mPairServer!!.pairedDeviceList
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
                    var ip = "ws://" + mPairedList[0].ip_address + ":" + mPairedList[0].port
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


            R.id.tv_btn_3 -> {
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

            R.id.tv_btn_5 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                mPairServer?.unPair(mPairedList[0], object : ECRHubResponseCallBack {
                    override fun onError(errorCode: String?, errorMsg: String?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "unPair failure:$errorMsg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onSuccess(data: PaymentResponseParams?) {
                        mClient.disConnect()
                        runOnUiThread {
                            ll_layout1.visibility = View.GONE
//                            tv_text_1.text = ""
                            Toast.makeText(this@MainActivity, "Unpair Success!", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                })
            }

            R.id.tv_btn_10 -> {
                runOnUiThread {
                    Toast.makeText(this, "The APP is exiting...", Toast.LENGTH_LONG).show()
                }
                mPairServer?.stop()
                mClient.disConnect()
                finish()
            }

            R.id.tv_btn_6 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, PaymentActivity::class.java))
            }

            R.id.tv_btn_7 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, RefundActivity::class.java))
            }

            R.id.tv_btn_8 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                startActivity(Intent(applicationContext, QueryActivity::class.java))
            }

            R.id.tv_btn_9 -> {
                if (!isConnected) {
                    runOnUiThread {
                        Toast.makeText(this, "Server is not connect", Toast.LENGTH_LONG).show()
                    }
                    return
                }
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
}

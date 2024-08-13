package com.codepay.register.sdk.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.activity_main.tv_btn_usb_mode
import kotlinx.android.synthetic.main.activity_main.tv_btn_wlan_mode

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState, persistentState)
        tv_btn_wlan_mode.setOnClickListener {
            startActivity(Intent(applicationContext, WLanActivity::class.java))
        }
        tv_btn_usb_mode.setOnClickListener {
            startActivity(Intent(applicationContext, UsbActivity::class.java))
        }
    }
}
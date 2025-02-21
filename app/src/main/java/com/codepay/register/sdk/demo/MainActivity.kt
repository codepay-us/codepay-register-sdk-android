package com.codepay.register.sdk.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.activity_main.tv_btn_usb_mode
import kotlinx.android.synthetic.main.activity_main.tv_btn_wlan_mode

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(applicationContext, WLanActivity::class.java))
        finish()
//        tv_btn_wlan_mode.setOnClickListener {
//            startActivity(Intent(applicationContext, WLanActivity::class.java))
//        }
//        tv_btn_usb_mode.setOnClickListener {
//            startActivity(Intent(applicationContext, UsbActivity::class.java))
//        }
    }
}
package com.dvt.alwaysonscreen

import android.app.Activity
import android.os.Bundle

class KeepAliveActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
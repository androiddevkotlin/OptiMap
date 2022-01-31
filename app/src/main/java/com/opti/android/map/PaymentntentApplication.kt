package com.opti.android.map

import android.app.Application

class PaymentIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PaymentRepository.initialize(this)
    }
}
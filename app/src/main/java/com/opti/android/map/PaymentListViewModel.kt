package com.opti.android.map

import androidx.lifecycle.ViewModel

class PaymentListViewModel : ViewModel() {

    private val PaymentDataRepository = PaymentRepository.get()
    val PaymentDataListLiveData = PaymentDataRepository.getPayments()

    fun addPayment(PaymentData: PaymentData) {
        PaymentDataRepository.addPayment(PaymentData)
    }
}
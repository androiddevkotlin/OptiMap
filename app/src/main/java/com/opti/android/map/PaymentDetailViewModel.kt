package com.opti.android.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class PaymentDetailViewModel : ViewModel() {

    private val PaymentDataRepository = PaymentRepository.get()
    private val PaymentDataIdLiveData = MutableLiveData<UUID>()

    val PaymentDataLiveData: LiveData<PaymentData?> =
        Transformations.switchMap(PaymentDataIdLiveData) { PaymentDataId ->
            PaymentDataRepository.getPayment(PaymentDataId)
        }
    
    fun loadPaymentData(PaymentDataId: UUID) {
        PaymentDataIdLiveData.value = PaymentDataId
    }

    fun savePaymentData(PaymentData: PaymentData) {
        PaymentDataRepository.updatePayment(PaymentData)
    }
}
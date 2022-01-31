package com.opti.android.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.opti.android.map.database.PaymentDatabase
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "PaymentData-database"

class PaymentRepository private constructor(context: Context) {

    private val database : PaymentDatabase = Room.databaseBuilder(
        context.applicationContext,
        PaymentDatabase::class.java,
        DATABASE_NAME
    ).build()
    private val PaymentDataDao = database.PaymentDataDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getPayments(): LiveData<List<PaymentData>> = PaymentDataDao.getPayments()

    fun getPayment(id: UUID): LiveData<PaymentData?> = PaymentDataDao.getPayment(id)

    fun updatePayment(PaymentData: PaymentData) {
        executor.execute {
            PaymentDataDao.updatePayment(PaymentData)
        }
    }

    fun addPayment(PaymentData: PaymentData) {
        executor.execute {
            PaymentDataDao.addPayment(PaymentData)
        }
    }
    
    companion object {
        private var INSTANCE: PaymentRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = PaymentRepository(context)
            }
        }

        fun get(): PaymentRepository {
            return INSTANCE ?:
            throw IllegalStateException("PaymentDataRepository must be initialized")
        }
    }
}
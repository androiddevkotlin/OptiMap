package com.opti.android.map.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.opti.android.map.PaymentData
import java.util.*

@Dao
interface WalletDao {

    @Query("SELECT * FROM PaymentData")
    fun getPayments(): LiveData<List<PaymentData>>

    @Query("SELECT * FROM PaymentData WHERE id=(:id)")
    fun getPayment(id: UUID): LiveData<PaymentData?>

    @Update
    fun updatePayment(PaymentData: PaymentData)

    @Insert
    fun addPayment(PaymentData: PaymentData)
}
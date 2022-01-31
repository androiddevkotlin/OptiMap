package com.opti.android.map.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.opti.android.map.PaymentData

@Database(entities = [ PaymentData::class ], version=1)
@TypeConverters(PaymentTypeConverters::class)
abstract class PaymentDatabase : RoomDatabase() {

    abstract fun PaymentDataDao(): WalletDao
}
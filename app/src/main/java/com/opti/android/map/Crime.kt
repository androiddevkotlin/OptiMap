package com.opti.android.map

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class PaymentData(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var title: String = "",
                 var value: String = "",
                 var date: Date = Date(),
                 var isCredit: Boolean = false)
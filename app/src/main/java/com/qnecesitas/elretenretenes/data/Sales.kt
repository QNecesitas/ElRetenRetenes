package com.qnecesitas.elretenretenes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sales(
    @PrimaryKey(autoGenerate = false) var code: String ,
    @ColumnInfo(name = "codeProduct") var codeProduct: String ,
    @ColumnInfo(name = "totalPrice") var totalPrice: Double ,
    @ColumnInfo(name = "totalInv") var totalInv: Double ,
    @ColumnInfo(name = "amount") var amount: Int ,
    @ColumnInfo(name = "day") var day: Int ,
    @ColumnInfo(name = "month") var month: Int ,
    @ColumnInfo(name = "year") var year: Int
)
package com.qnecesitas.elretenretenes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Store(
    @PrimaryKey(autoGenerate = false) var code:String,
    @ColumnInfo(name = "brand") var brand:String,
    @ColumnInfo(name = "amount") var amount:Int,
    @ColumnInfo(name = "buyPrice") var buyPrice:Int,
    @ColumnInfo(name = "salePrice") var salePrice:Int,
    @ColumnInfo(name = "descr") var descr:String,
    @ColumnInfo(name = "deficit") var deficit:Int,
    @ColumnInfo(name = "size") var size:Int,
    @ColumnInfo(name = "location") var location:String,

)
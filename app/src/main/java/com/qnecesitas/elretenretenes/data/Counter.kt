package com.qnecesitas.elretenretenes.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Counter(
    @PrimaryKey(autoGenerate = false) var code:String ,
    @ColumnInfo(name = "brand") var brand:String ,
    @ColumnInfo(name = "amount") var amount:Int ,
    @ColumnInfo(name = "buyPrice") var buyPrice:Double ,
    @ColumnInfo(name = "salePrice") var salePrice:Double ,
    @ColumnInfo(name = "descr") var descr:String ,
    @ColumnInfo(name = "deficit") var deficit:Int ,
    @ColumnInfo(name = "size") var size:String ,
    @ColumnInfo(name = "location") var location:String ,

    )
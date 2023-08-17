package com.qnecesitas.elretenretenes.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SalesDao {

    @Query("INSERT INTO Sales VALUES (:code,:codeProduct,:totalPrice,:totalInv,:amount,:day,:month,:year)")
    fun insertSales(
        code: String ,
        codeProduct: String ,
        totalPrice: Int ,
        totalInv: Int ,
        amount: Int ,
        day: Int ,
        month: Int ,
        year: Int
    )

}
package com.qnecesitas.elretenretenes.database

import androidx.room.Dao
import androidx.room.Query
import com.qnecesitas.elretenretenes.data.Sales

@Dao
interface SalesDao {

    @Query("INSERT INTO Sales VALUES (:code,:codeProduct,:totalPrice,:totalInv,:amount,:day,:month,:year)")
    fun insertSales(
        code: String ,
        codeProduct: String ,
        totalPrice: Double ,
        totalInv: Double ,
        amount: Int ,
        day: Int ,
        month: Int ,
        year: Int
    )

    @Query("SELECT * FROM Sales WHERE  year=:year ORDER BY month ASC")
    suspend fun selectOrderY(year: Int) : List<Sales>

    @Query("SELECT * FROM Sales WHERE day = :day AND month=:month AND year=:year ")
    suspend fun selectOrderD(day: Int, month: Int, year: Int) : List<Sales>

    @Query("SELECT * FROM Sales WHERE month=:month AND year=:year ORDER BY day ASC")
    suspend fun selectOrdersM(month: Int,year: Int) : List<Sales>

    @Query("SELECT * FROM Sales WHERE year=:year AND (month=:month OR month=:monthLast)   ORDER BY day ASC")
    suspend fun fetchSalesStatistics(month: Int,monthLast:Int,year: Int):List<Sales>

}
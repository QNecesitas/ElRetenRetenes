package com.qnecesitas.elretenretenes.database

import androidx.room.Dao
import androidx.room.Query
import com.qnecesitas.elretenretenes.data.Counter
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {

    @Query("SELECT * FROM Counter WHERE code = :code")
    suspend fun selectDuplicate(code: String): List<Counter>

    @Query("INSERT INTO Counter VALUES (:code,:location,:amount,:buyPrice,:salePrice,:descr,:deficit,:size,:brand)")
    suspend fun insertReten(
        code: String ,
        location: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        descr: String ,
        deficit: Int ,
        size: String ,
        brand: String
    )

    @Query("SELECT * FROM Counter ORDER BY CAST (SUBSTR(size,1,INSTR(size,'*')-1) AS INTEGER)")
    fun fetchReten(): Flow<List<Counter>>

    @Query("UPDATE Counter SET code=:code, location=:location, amount=:amount, buyPrice=:buyPrice, salePrice=:salePrice,descr=:descr,deficit =:deficit, size =:size, brand=:brand WHERE code=:code ")
    suspend fun updateReten(
        code: String ,
        location: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        descr: String ,
        deficit: Int ,
        size: String ,
        brand: String
    )

    @Query("UPDATE Counter SET amount=:amount WHERE code=:code")
    suspend fun updateAmount(code: String , amount: Int)

    @Query("DELETE FROM Counter WHERE code=:code")
    suspend fun deleteReten(code: String)

    @Query("SELECT code FROM Store WHERE code = :code")
    suspend fun selectDuplicateStore(code: String): String?

    @Query("UPDATE Store SET amount=:amount WHERE code=:code")
    suspend fun updateAmountStore(code: String , amount: Int)

    @Query("SELECT amount FROM Store WHERE code=:code")
    suspend fun fetchAmountStore(code: String): Int

    @Query("INSERT INTO Store VALUES (:code,:location,:amount,:buyPrice,:salePrice,:descr,:deficit,:size,:brand)")
    suspend fun insertStore(
        code: String ,
        location: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        descr: String ,
        deficit: Int ,
        size: String ,
        brand: String
    )

    @Query("INSERT INTO Sales VALUES (:code,:codeProduct,:totalPrice,:totalInv,:amount,:day,:month,:year)")
    suspend fun insertSales(
        code: String ,
        codeProduct: String ,
        totalPrice: Double ,
        totalInv: Double ,
        amount: Int ,
        day: Int ,
        month: Int ,
        year: Int
    )

    @Query("SELECT COUNT(*) FROM Counter WHERE amount <= deficit")
    suspend fun getDeficitCounter(): Int

    @Query("SELECT * FROM Counter WHERE amount <= deficit ORDER BY CAST (SUBSTR(size,1,INSTR(size,'*')-1) AS INTEGER)")
    fun getDeficitCounters(): Flow<List<Counter>>

}


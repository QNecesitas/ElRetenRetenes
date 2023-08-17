package com.qnecesitas.elretenretenes.database

import androidx.room.Dao
import androidx.room.Query
import com.qnecesitas.elretenretenes.data.Store
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Query("SELECT * FROM Store WHERE code = :code")
    suspend fun selectDuplicate(code: String): List<Store>

    @Query("INSERT INTO Store VALUES (:code,:location,:amount,:buyPrice,:salePrice,:descr,:deficit,:size,:brand)")
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

    @Query("SELECT * FROM Store")
    fun fetchReten(): Flow<List<Store>>

    @Query("UPDATE Store SET code=:code, location=:location, amount=:amount, buyPrice=:buyPrice, salePrice=:salePrice,descr=:descr,deficit =:deficit, size =:size, brand=:brand WHERE code=:code ")
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

    @Query("UPDATE Store SET amount=:amount WHERE code=:code")
    suspend fun updateAmount(code: String , amount: Int)

    @Query("DELETE FROM Store WHERE code=:code")
    suspend fun deleteReten(code: String)

    @Query("SELECT code FROM Counter WHERE code = :code")
    suspend fun selectDuplicateCounter(code: String): String?

    @Query("UPDATE Counter SET amount=:amount WHERE code=:code")
    suspend fun updateAmountCounter(code: String , amount: Int)

    @Query("SELECT amount FROM Counter WHERE code=:code")
    suspend fun fetchAmountCounter(code: String): Int

    @Query("INSERT INTO Counter VALUES (:code,:location,:amount,:buyPrice,:salePrice,:descr,:deficit,:size,:brand)")
    suspend fun insertCounter(
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


}
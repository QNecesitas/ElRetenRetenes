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
    suspend fun insertReten(code: String, location: String , amount: Int, buyPrice: Double, salePrice: Double, descr: String, deficit: Int, size: String, brand: String)

    @Query("SELECT * FROM Counter")
    fun fetchReten(): Flow<List<Counter>>
    @Query("UPDATE Counter SET code=:code, location=:location, amount=:amount, buyPrice=:buyPrice, salePrice=:salePrice,descr=:descr,deficit =:deficit, size =:size, brand=:brand WHERE code=:code ")
    suspend fun updateReten(code: String,location: String, amount: Int, buyPrice: Double, salePrice: Double, descr: String, deficit: Int, size: String, brand: String)

    @Query("UPDATE Counter SET amount=:amount WHERE code=:code")
    suspend fun updateAmount(code: String,amount: Int)

    @Query("DELETE FROM Counter WHERE code=:code")
    suspend fun deleteReten(code: String)
}

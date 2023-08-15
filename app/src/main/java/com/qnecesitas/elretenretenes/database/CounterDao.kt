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
}

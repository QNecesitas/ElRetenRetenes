package com.qnecesitas.elretenretenes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.data.Sales
import com.qnecesitas.elretenretenes.data.Store

@Database(
    entities = [Store::class , Sales::class , Counter::class] ,
    version = 4 ,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun storeDao(): StoreDao
    abstract fun salesDao(): SalesDao
    abstract fun counterDao(): CounterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext ,
                    AppDatabase::class.java ,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }

}
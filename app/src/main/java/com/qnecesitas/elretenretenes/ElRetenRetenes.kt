package com.qnecesitas.elretenretenes

import android.app.Application
import com.qnecesitas.elretenretenes.database.AppDatabase

class ElRetenRetenes: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
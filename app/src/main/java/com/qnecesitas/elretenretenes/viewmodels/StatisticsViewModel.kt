package com.qnecesitas.elretenretenes.viewmodels

import com.qnecesitas.elretenretenes.ElRetenRetenes
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.data.Sales

class Repository(application: ElRetenRetenes) {

    val salesDao= application.database.salesDao()

    suspend fun fetchOrdersY(year: Int) : MutableList<Sales>{
        return salesDao.selectOrderY(year).toMutableList()
    }

    suspend fun fetchOrdersD(year:Int,month: Int,day: Int) : MutableList<Sales>{
        return salesDao.selectOrderD(day,month,year).toMutableList()
    }

    suspend fun fetchOrdersM(year: Int,month: Int) : MutableList<Sales>{
        return salesDao.selectOrdersM(month,year).toMutableList()
    }

    suspend fun fetchSalesStatistics(month: Int,monthLast:Int,year:Int):MutableList<Sales>{
        return salesDao.fetchSalesStatistics(month,monthLast,year).toMutableList()
    }

}
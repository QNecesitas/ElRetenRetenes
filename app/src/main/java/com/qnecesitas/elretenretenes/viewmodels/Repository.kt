package com.qnecesitas.elretenretenes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qnecesitas.elretenretenes.ElRetenRetenes
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.data.Sales
import com.qnecesitas.elretenretenes.data.Store

class Repository(application: ElRetenRetenes) {

    //List counter
    private val _listCounter = MutableLiveData<MutableList<Counter>>()
    val listCounter: LiveData<MutableList<Counter>> get() = _listCounter

    private val _listStore = MutableLiveData<MutableList<Store>>()
    val listStore: LiveData<MutableList<Store>> get() = _listStore

    //List counter filter
    private val _listCounterFilter = MutableLiveData<MutableList<Counter>>()
    val listCounterFilter: LiveData<MutableList<Counter>> get() = _listCounterFilter

    //List driver
    private val _listStoreFilter = MutableLiveData<MutableList<Store>>()
    val listStoreFilter: LiveData<MutableList<Store>> get() = _listStoreFilter


    val salesDao = application.database.salesDao()
    val counterDao = application.database.counterDao()
    val storeDao = application.database.storeDao()

    suspend fun fetchOrdersY(year: Int): MutableList<Sales> {
        return salesDao.selectOrderY(year).toMutableList()
    }

    suspend fun fetchOrdersD(year: Int , month: Int , day: Int): MutableList<Sales> {
        return salesDao.selectOrderD(day , month , year).toMutableList()
    }

    suspend fun fetchOrdersM(year: Int , month: Int): MutableList<Sales> {
        return salesDao.selectOrdersM(month , year).toMutableList()
    }

    suspend fun fetchSalesStatistics(month: Int , monthLast: Int , year: Int): MutableList<Sales> {
        return salesDao.fetchSalesStatistics(month , monthLast , year).toMutableList()
    }

    suspend fun getAllRetenCounter() {
        counterDao.getDeficitCounters().collect {
            _listCounter.postValue(it as MutableList<Counter>?)
        }
    }

    suspend fun updateAmount(
        code: String ,
        amount: Int
    ) {
        counterDao.updateAmount(
            code ,
            amount
        )
    }

    fun filterByTextCounter(text: String) {
        if (text.trim().isNotEmpty()) {
            val filterList = _listCounter.value?.filter {
                it.size.contains(text , ignoreCase = true)

            }?.toMutableList()

            if (filterList != null) {
                _listCounterFilter.postValue(filterList!!)
            }

        } else {
            _listCounterFilter.postValue(listCounter.value)
        }
    }

    fun filterByTextStore(text: String) {
        if (text.trim().isNotEmpty()) {
            val filterList = _listStore.value?.filter {
                it.size.contains(text , ignoreCase = true)

            }?.toMutableList()

            if (filterList != null) {
                _listStoreFilter.postValue(filterList!!)
            }

        } else {
            _listStoreFilter.postValue(listStore.value)
        }
    }

    suspend fun getAllRetenStore() {
        storeDao.getDeficitStore().collect {
            _listStore.postValue(it as MutableList<Store>?)
        }
    }

    suspend fun updateAmountStore(
        code: String ,
        amount: Int
    ) {
        storeDao.updateAmount(
            code ,
            amount
        )
    }

    suspend fun getDeficitCounter(): Int {
        return counterDao.getDeficitCounter()
    }

    suspend fun getDeficitStore(): Int {
        return storeDao.getDeficitStores()
    }

}
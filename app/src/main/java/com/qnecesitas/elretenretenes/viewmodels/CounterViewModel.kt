package com.qnecesitas.elretenretenes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.database.CounterDao


class CounterViewModel(private val counterDao: CounterDao) : ViewModel() {

    //List counter
    private val _listCounter = MutableLiveData<MutableList<Counter>>()
    val listCounter: LiveData<MutableList<Counter>> get() = _listCounter

    //List driver
    private val _listCounterFilter = MutableLiveData<MutableList<Counter>>()
    val listCounterFilter: LiveData<MutableList<Counter>> get() = _listCounterFilter


    suspend fun selectDuplicate(code: String): Boolean {
        return counterDao.selectDuplicate(code).isNotEmpty()
    }

    suspend fun addReten(
        code: String ,
        location: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        descr: String ,
        deficit: Int ,
        size: String ,
        brand: String
    ) {
        counterDao.insertReten(
            code ,
            location ,
            amount ,
            buyPrice ,
            salePrice ,
            descr ,
            deficit ,
            size ,
            brand
        )


    }

    suspend fun getAllReten() {
        counterDao.fetchReten().collect {
            _listCounter.postValue(it as MutableList<Counter>?)
        }
    }

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
    ) {
        counterDao.updateReten(
            code ,
            location ,
            amount ,
            buyPrice ,
            salePrice ,
            descr ,
            deficit ,
            size ,
            brand
        )


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

    fun filterByText(text: String) {
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

    suspend fun deleteReten(
        code: String
    ) {
        counterDao.deleteReten(
            code
        )
    }

    suspend fun isDuplicateStore(code: String): String? {
        return counterDao.selectDuplicateStore(code)
    }

    suspend fun updateAmountStore(
        code: String ,
        amount: Int
    ) {
        counterDao.updateAmountStore(
            code ,
            amount
        )
    }

    suspend fun getAmountStore(code: String): Int {
        return counterDao.fetchAmountStore(code)
    }

    suspend fun addStore(
        code: String ,
        location: String ,
        amount: Int ,
        buyPrice: Double ,
        salePrice: Double ,
        descr: String ,
        deficit: Int ,
        size: String ,
        brand: String
    ) {
        counterDao.insertStore(
            code ,
            location ,
            amount ,
            buyPrice ,
            salePrice ,
            descr ,
            deficit ,
            size ,
            brand
        )


    }

    suspend fun addSales(
        code: String ,
        codeProduct: String ,
        totalPrice: Double ,
        totalInv: Double ,
        amount: Int ,
        day: Int ,
        month: Int ,
        year: Int
    ) {
        counterDao.insertSales(
            code ,
            codeProduct ,
            totalPrice ,
            totalInv ,
            amount ,
            day ,
            month ,
            year
        )
    }

    suspend fun getDeficitCounter(): Int {
        return counterDao.getDeficitCounter()
    }

    suspend fun getDeficitStore(): Int {
        return counterDao.getDeficitStore()
    }


}

class CounterViewModelFactory(
    private val counterDao: CounterDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CounterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CounterViewModel(counterDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}
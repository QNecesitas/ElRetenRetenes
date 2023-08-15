package com.qnecesitas.elretenretenes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qnecesitas.elretenretenes.data.Counter
import com.qnecesitas.elretenretenes.database.CounterDao

class CounterViewModel(private val counterDao: CounterDao):ViewModel() {

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
        counterDao.fetchReten().collect() {
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
        code: String,
        amount: Int
    ){
        counterDao.updateAmount(
            code,
            amount
        )
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
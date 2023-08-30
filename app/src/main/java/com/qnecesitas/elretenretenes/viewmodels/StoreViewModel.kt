package com.qnecesitas.elretenretenes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qnecesitas.elretenretenes.data.Store
import com.qnecesitas.elretenretenes.database.StoreDao


class StoreViewModel(private val storeDao: StoreDao) : ViewModel() {

    //List counter
    private val _listStore = MutableLiveData<MutableList<Store>>()
    val listStore: LiveData<MutableList<Store>> get() = _listStore

    //List driver
    private val _listStoreFilter = MutableLiveData<MutableList<Store>>()
    val listStoreFilter: LiveData<MutableList<Store>> get() = _listStoreFilter


    suspend fun selectDuplicate(code: String): Boolean {
        return storeDao.selectDuplicate(code).isNotEmpty()
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
        storeDao.insertReten(
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
        storeDao.fetchReten().collect {
            _listStore.postValue(it as MutableList<Store>?)
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
        storeDao.updateReten(
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
        storeDao.updateAmount(
            code ,
            amount
        )
    }

    fun filterByText(text: String) {
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

    suspend fun deleteReten(
        code: String
    ) {
        storeDao.deleteReten(
            code
        )
    }

    suspend fun isDuplicateCounter(code: String): String? {
        return storeDao.selectDuplicateCounter(code)
    }

    suspend fun updateAmountCounter(
        code: String ,
        amount: Int
    ) {
        storeDao.updateAmountCounter(
            code ,
            amount
        )
    }

    suspend fun getAmountCounter(code: String): Int {
        return storeDao.fetchAmountCounter(code)
    }

    suspend fun addCounter(
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
        storeDao.insertCounter(
            code,
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
        storeDao.insertSales(
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

}

class StoreViewModelFactory(
    private val storeDao: StoreDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreViewModel(storeDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}
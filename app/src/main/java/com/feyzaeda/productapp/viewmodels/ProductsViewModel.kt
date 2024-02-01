package com.feyzaeda.productapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feyzaeda.productapp.models.Products
import com.feyzaeda.productapp.repo.ProductsDaoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val mRepo: ProductsDaoRepo) : ViewModel() {
    private var _productsList = MutableLiveData<List<Products>>()
    val productsList: LiveData<List<Products>> get() = _productsList
    val filteredCarListLiveData = MutableLiveData<List<Products>>()
    private var minPrice: Float? = null
    private var maxPrice: Float? = null

    init {
        getProductsList()
        _productsList = mRepo.getProductsList()
    }

    private fun getProductsList() = viewModelScope.launch {
        mRepo.allProducts()
    }

    fun productsListUpdate() {
        val emptyList: List<Products> = mutableListOf()
        _productsList.value = emptyList
        getProductsList()
        _productsList = mRepo.getProductsList()
    }

    fun searchProduct(query: String) {
        val filteredList = mRepo.getProductsList().value?.filter {
            it.name!!.contains(query, ignoreCase = true)
        }
        filteredCarListLiveData.value = filteredList!!
    }

    private fun applyPriceFilter(list: List<Products>): List<Products> {
        return list.filter { car ->
            val carPrice = car.price // Araba nesnesinde fiyatı temsil eden bir öğe
            (minPrice == null || carPrice!! >= minPrice!!.toString()) && (maxPrice == null || carPrice!! <= maxPrice!!.toString())
        }
    }

    fun updatePriceFilter(min: Float?, max: Float?) {
        minPrice = min
        maxPrice = max
        filteredCarListLiveData.value = applyPriceFilter(_productsList.value!!)
    }
}
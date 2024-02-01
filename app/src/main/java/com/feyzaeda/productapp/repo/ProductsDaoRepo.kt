package com.feyzaeda.productapp.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.feyzaeda.productapp.data.remote.APIService
import com.feyzaeda.productapp.models.Products
import javax.inject.Inject

class ProductsDaoRepo @Inject constructor(private val mDao: APIService) {
    private var productsList: MutableLiveData<List<Products>> = MutableLiveData()

    fun getProductsList(): MutableLiveData<List<Products>> {
        return productsList
    }


    suspend fun allProducts(){
        val response = mDao.getProductsList()
        if(response.isSuccessful){
            response.body()?.let {
                Log.e("api1","girdi")
                val list = response.body()
                productsList.value = list!!
            }

        }else{
            Log.e("api1","hata")
        }
    }
}
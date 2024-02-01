package com.feyzaeda.productapp.data.remote

import com.feyzaeda.productapp.models.Products
import retrofit2.Response
import retrofit2.http.GET

interface APIService {

    @GET("products")
    suspend fun getProductsList() : Response<List<Products>>
}
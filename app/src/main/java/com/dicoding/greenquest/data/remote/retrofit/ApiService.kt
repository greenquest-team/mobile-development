package com.dicoding.greenquest.data.remote.retrofit

import com.dicoding.greenquest.data.remote.response.WasteTypeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("waste-types")
    suspend fun getWasteTypes(
        @Query("type_name") query: String
    ): WasteTypeResponse
}
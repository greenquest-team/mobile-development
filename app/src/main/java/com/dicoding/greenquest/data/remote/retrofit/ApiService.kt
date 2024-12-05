package com.dicoding.greenquest.data.remote.retrofit

import com.dicoding.greenquest.data.remote.response.LoginResponse
import com.dicoding.greenquest.data.remote.response.StoryResponse
import com.dicoding.greenquest.data.remote.response.WasteTypeResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("waste-types")
    suspend fun getWasteTypes(
        @Query("type_name") query: String
    ): WasteTypeResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse
}
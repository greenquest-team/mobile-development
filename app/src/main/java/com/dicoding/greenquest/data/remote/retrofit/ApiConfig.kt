package com.dicoding.greenquest.data.remote.retrofit

import com.dicoding.greenquest.BuildConfig
import com.dicoding.greenquest.data.remote.ApiType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private var token: String? = null

        fun setToken(newToken: String) {
            token = newToken
        }

        fun getApiService(apiType: ApiType): ApiService {
            val baseUrl = when (apiType) {
                ApiType.REAL -> BuildConfig.BASE_URL
                ApiType.DUMMY -> BuildConfig.DUMMY_URL
            }
            val loggingInterceptor = if(BuildConfig.DEBUG) { HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY) } else { HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.NONE) }
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(requestHeaders)
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
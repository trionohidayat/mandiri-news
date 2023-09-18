package com.android.mandirinews.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://newsapi.org/"

    private val apiInterceptor = ApiInterceptor(ApiKeyProviderImpl())

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(apiInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
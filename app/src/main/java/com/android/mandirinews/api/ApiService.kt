package com.android.mandirinews.api

import com.android.mandirinews.ResNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("page") page: Int
    ): Call<ResNews>

    @GET("v2/everything")
    fun getEverything(
        @Query("q") query: String
    ): Call<ResNews>
}
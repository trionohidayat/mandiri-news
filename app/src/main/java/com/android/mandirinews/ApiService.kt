package com.android.mandirinews

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers("X-Api-Key: 1f76d855542f48cdb1d8db8ce6a88384")
    @GET("v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("page") page: Int
    ): Call<ResNews>

    @Headers("X-Api-Key: 1f76d855542f48cdb1d8db8ce6a88384")
    @GET("v2/everything")
    fun getEverything(
        @Query("q") query: String
    ): Call<ResNews>
}
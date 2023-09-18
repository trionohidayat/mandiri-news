package com.android.mandirinews.api

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(private val apiKeyProvider: ApiKeyProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val apiKey = apiKeyProvider.getApiKey()

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()

        val response = chain.proceed(newRequest)

        if (!response.isSuccessful) {
            apiKeyProvider.changeApiKey()
            val retryRequest = newRequest.newBuilder()
                .header("Authorization", "Bearer ${apiKeyProvider.getApiKey()}")
                .build()
            return chain.proceed(retryRequest)
        }

        return response
    }
}
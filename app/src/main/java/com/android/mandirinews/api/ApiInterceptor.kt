package com.android.mandirinews.api

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(private val apiKeyProvider: ApiKeyProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val apiKey = apiKeyProvider.getApiKey()

        val newRequest = originalRequest.newBuilder()
            .header("X-Api-Key", apiKey) // Ganti Authorization -> X-Api-Key
            .header("User-Agent", "Mozilla/5.0 (Android 10; Mobile; rv:90.0) Gecko/90.0 Firefox/90.0")
            .build()

        return chain.proceed(newRequest)
    }
}
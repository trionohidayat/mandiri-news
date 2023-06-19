package com.android.mandirinews

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(private val apiKeyProvider: ApiKeyProvider) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Mengambil API Key dari ApiKeyProvider
        val apiKey = apiKeyProvider.getApiKey()

        // Membuat permintaan baru dengan Header yang diperbarui
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .build()

        // Melanjutkan permintaan ke network
        val response = chain.proceed(newRequest)

        // Cek jika permintaan gagal (kode respon di luar rentang 200-299)
        if (!response.isSuccessful) {
            // Ganti API Key pada ApiKeyProvider
            apiKeyProvider.changeApiKey()
            // Mengulangi permintaan dengan API Key yang baru
            val retryRequest = newRequest.newBuilder()
                .header("Authorization", "Bearer ${apiKeyProvider.getApiKey()}")
                .build()
            return chain.proceed(retryRequest)
        }

        return response
    }
}
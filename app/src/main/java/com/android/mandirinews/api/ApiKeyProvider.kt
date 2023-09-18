package com.android.mandirinews.api

interface ApiKeyProvider {
    fun getApiKey(): String
    fun changeApiKey()
}
package com.android.mandirinews

interface ApiKeyProvider {
    fun getApiKey(): String
    fun changeApiKey()
}
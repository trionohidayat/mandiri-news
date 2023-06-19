package com.android.mandirinews

class ApiKeyProviderImpl : ApiKeyProvider {

    private val apiKeys = mutableListOf(
        "1f76d855542f48cdb1d8db8ce6a88384",
        "b0754a0f62bb4501ab9164db00484e41"
    )

    private var currentApiKeyIndex = 0

    override fun getApiKey(): String {
        return apiKeys[currentApiKeyIndex]
    }

    override fun changeApiKey() {
        currentApiKeyIndex = (currentApiKeyIndex + 1) % apiKeys.size
    }
}

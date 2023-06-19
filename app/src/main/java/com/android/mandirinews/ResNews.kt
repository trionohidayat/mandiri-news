package com.android.mandirinews

data class ResNews(
    val status: String,
    val code: String,
    val message: String,
    val totalResults: Int,
    val articles: List<Article>
)

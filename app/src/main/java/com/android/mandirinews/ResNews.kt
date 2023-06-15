package com.android.mandirinews

data class ResNews(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val author: String,
    val url: String
)

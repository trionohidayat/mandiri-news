package com.android.mandirinews

data class ResNews(
    val status: String,
    val code: String,
    val message: String,
    val totalResults: Int,
    val articles: List<Article>
)

data class Article(
    val title: String,
    val author: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: String,
    val url: String
)

package com.android.mandirinews

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(

    @PrimaryKey val title: String,
    val author: String?,
    val description: String,
    val publishedAt: String,
    val urlToImage: String?,
    val url: String
)
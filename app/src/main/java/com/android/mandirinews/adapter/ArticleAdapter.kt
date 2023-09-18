package com.android.mandirinews.adapter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.mandirinews.Article
import com.android.mandirinews.R
import com.android.mandirinews.WebViewActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter(
    val articles: MutableList<Article>,
    private val preferences: SharedPreferences,
    private val loadMoreListener: LoadMoreListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeItem = 0
    private val viewTypeLoading = 1

    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeItem) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_article, parent, false)
            ItemViewHolder(itemView)
        } else {
            val loadingView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(loadingView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val article = articles[position]
            holder.bind(article)
        } else if (holder is LoadingViewHolder) {
            holder.showLoading()
            loadMoreListener.onLoadMore()
        }
    }

    override fun getItemCount(): Int {
        return articles.size + if (isLoading) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < articles.size) {
            viewTypeItem
        } else {
            viewTypeLoading
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.textTitle)
        private val descriptionText: TextView = itemView.findViewById(R.id.textDescription)
        private val dateText: TextView = itemView.findViewById(R.id.textDate)
        private val imageArticle: ImageView = itemView.findViewById(R.id.imageArticle)

        fun bind(article: Article) {
            titleText.text = article.title
            descriptionText.text = article.description

            val formattedDate = formatDate(article.publishedAt)
            dateText.text = formattedDate

            Glide.with(itemView)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder)
                .into(imageArticle)

            itemView.setOnClickListener {

                when (preferences.getString("article_access", "web_view")) {
                    "web_view" -> openArticleInWebView(article.url)
                    "google_chrome" -> openArticleInChrome(article.url)
                }
            }
        }

        private fun openArticleInWebView(url: String) {
            val intent = Intent(itemView.context, WebViewActivity::class.java)
            intent.putExtra("url", url)
            itemView.context.startActivity(intent)
        }

        private fun openArticleInChrome(url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")

            try {
                itemView.context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                intent.setPackage(null)
                itemView.context.startActivity(intent)
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layoutLoad: LinearLayout = itemView.findViewById(R.id.layoutLoad)

        fun showLoading() {
            layoutLoad.visibility = View.VISIBLE
        }
    }

    interface LoadMoreListener {
        fun onLoadMore()
    }

    private fun formatDate(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(dateString)
        return date?.let { outputFormat.format(it) }
    }
}


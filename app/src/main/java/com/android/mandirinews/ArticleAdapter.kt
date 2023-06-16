package com.android.mandirinews

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter(
    private val articles: MutableList<Article>,
    private val loadMoreListener: LoadMoreListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
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
            VIEW_TYPE_ITEM
        } else {
            VIEW_TYPE_LOADING
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder implementation for item views
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
                .placeholder(R.drawable.news)
                .into(imageArticle)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, WebViewActivity::class.java)
                intent.putExtra("url", article.url)
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


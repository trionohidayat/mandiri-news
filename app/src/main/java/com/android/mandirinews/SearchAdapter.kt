package com.android.mandirinews

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        private val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        private val textDate: TextView = itemView.findViewById(R.id.textDate)
        private val imageArticle: ImageView = itemView.findViewById(R.id.imageArticle)

        fun bind(article: Article) {
            textTitle.text = article.title
            textDescription.text = article.description
            textDate.text = article.publishedAt

            // Load image using your preferred image loading library
            // Example using Glide:
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.placeholder)
                .into(imageArticle)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, WebViewActivity::class.java)
                intent.putExtra("url", article.url)
                itemView.context.startActivity(intent)
            }
        }
    }
}



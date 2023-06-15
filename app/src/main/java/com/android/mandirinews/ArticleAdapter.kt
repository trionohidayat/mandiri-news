package com.android.mandirinews

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.textTitle)
        private val authorText: TextView = itemView.findViewById(R.id.textAuthor)
        private val descriptionText: TextView = itemView.findViewById(R.id.textDescription)
        private val dateText: TextView = itemView.findViewById(R.id.textDate)
        private val imageArticle: ImageView = itemView.findViewById(R.id.imageArticle)


        fun bind(article: Article) {
            titleText.text = article.title
            authorText.text = article.author
            descriptionText.text = article.description

            val formattedDate = formatDate(article.publishedAt)
            dateText.text = formattedDate

            Glide.with(itemView)
                .load(article.urlToImage)
                .into(imageArticle)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, WebViewActivity::class.java)
                intent.putExtra("url", article.url)
                itemView.context.startActivity(intent)
            }
        }
    }

    fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }
}
package com.android.mandirinews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mandirinews.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var rvCategory: RecyclerView
    private lateinit var rvArticle: RecyclerView

    private val country = "us"
    var category = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar
        rvCategory = binding.rvCategory
        rvArticle = binding.rvArticle

        rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val categoryList =
            listOf("Business", "Entertainment", "Health", "Science", "Sports", "Technology")
        val adapter = CategoryAdapter(categoryList)
        rvCategory.adapter = adapter

        adapter.setOnItemClickListener(object : CategoryAdapter.OnItemClickListener {
            override fun onItemClick(category: String) {
                this@MainActivity.category = category.lowercase(Locale.getDefault())
                loadNewsData()
            }
        })

        rvArticle.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    override fun onStart() {
        loadNewsData()
        super.onStart()
    }

    private fun loadNewsData() {
        progressBar.visibility = View.VISIBLE

        val call: Call<ResNews> = RetrofitClient.apiService.getTopHeadlines(country, category)
        call.enqueue(object : Callback<ResNews> {
            override fun onResponse(call: Call<ResNews>, response: Response<ResNews>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val newsResponse: ResNews? = response.body()
                    val articles: List<Article>? = newsResponse?.articles

                    val adapter = articles?.let { ArticleAdapter(it) }
                    rvArticle.layoutManager = LinearLayoutManager(this@MainActivity)
                    rvArticle.adapter = adapter
                } else {
                    Log.e("API Response", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResNews>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("API Call", "Failed: ${t.message}")
            }
        })
    }
}
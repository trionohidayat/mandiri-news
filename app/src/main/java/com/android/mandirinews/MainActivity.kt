package com.android.mandirinews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mandirinews.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val country = "id"
    var category = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val categoryList =
            listOf("Business", "Entertainment", "Health", "Science", "Sports", "Technology")
        val adapter = CategoryAdapter(categoryList)
        binding.rvCategory.adapter = adapter

        adapter.setOnItemClickListener(object : CategoryAdapter.OnItemClickListener {
            override fun onItemClick(category: String) {
                this@MainActivity.category = category.lowercase(Locale.getDefault())
                loadNewsData()
            }
        })
    }

    override fun onStart() {
        loadNewsData()
        super.onStart()
    }

    private fun loadNewsData() {
        val call: Call<ResNews> = RetrofitClient.apiService.getTopHeadlines(country, category)
        call.enqueue(object : Callback<ResNews> {
            override fun onResponse(call: Call<ResNews>, response: Response<ResNews>) {
                if (response.isSuccessful) {
                    val newsResponse: ResNews? = response.body()
                    val articles: List<Article>? = newsResponse?.articles

                    val adapter = articles?.let { ArticleAdapter(it) }
                    binding.rvArticle.layoutManager = LinearLayoutManager(this@MainActivity)
                    binding.rvArticle.adapter = adapter
                } else {
                    Log.e("API Response", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResNews>, t: Throwable) {
                Log.e("API Call", "Failed: ${t.message}")
            }
        })
    }
}
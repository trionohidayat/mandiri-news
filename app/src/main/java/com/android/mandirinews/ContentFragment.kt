package com.android.mandirinews

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentFragment : Fragment(), ArticleAdapter.LoadMoreListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var rvArticle: RecyclerView

    private val country = "us"
    private var category = ""
    private var currentPage = 1

    private var isLoading = false
    private var isLastPage = false

    private var adapter: ArticleAdapter? = null
    private val articles: MutableList<Article> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        rvArticle = view.findViewById(R.id.rvArticle)

        rvArticle.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))

        arguments?.getString("category")?.let { category ->
            this.category = category
        }

        setupRecyclerView()
        loadNewsData()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        rvArticle.layoutManager = layoutManager

        adapter = ArticleAdapter(articles, this)
        rvArticle.adapter = adapter

        rvArticle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE
                    ) {
                        currentPage++
                        loadNewsData()
                    }
                }
            }
        })
    }

    private fun loadNewsData() {
        progressBar.visibility = View.VISIBLE

        val call: Call<ResNews> = RetrofitClient.apiService.getTopHeadlines(country, category, currentPage)
        call.enqueue(object : Callback<ResNews> {
            override fun onResponse(call: Call<ResNews>, response: Response<ResNews>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val newsResponse: ResNews? = response.body()
                    val newArticles: List<Article>? = newsResponse?.articles

                    newArticles?.let {
                        if (it.isNotEmpty()) {
                            articles.addAll(it)
                            adapter?.notifyDataSetChanged()
                        } else {
                            isLastPage = true
                        }
                    }
                } else {
                    Log.e("API Response", "Error: ${response.code()}")
                }

                isLoading = false
            }

            override fun onFailure(call: Call<ResNews>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("API Call", "Failed: ${t.message}")

                isLoading = false
            }
        })
    }

    override fun onLoadMore() {
        isLoading = true
        currentPage++
        loadNewsData()
    }
}

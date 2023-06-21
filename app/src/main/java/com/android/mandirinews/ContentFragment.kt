package com.android.mandirinews

import android.content.Context
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.mandirinews.databinding.FragmentContentBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentFragment : Fragment(), ArticleAdapter.LoadMoreListener {

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressBar: ProgressBar
    private lateinit var textMessage: TextView
    private lateinit var rvArticle: RecyclerView
    private lateinit var buttonRetry: Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val country = "us"
    private var category = ""
    private var currentPage = 1

    private var isLoading = false
    private var isLastPage = false

    private lateinit var adapter: ArticleAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val articles: MutableList<Article> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar
        textMessage = binding.textMessage
        rvArticle = binding.rvArticle
        buttonRetry = binding.includeError.buttonRetry
        swipeRefreshLayout = binding.swipeRefreshLayout

        val layoutManager = LinearLayoutManager(requireContext())
        rvArticle.layoutManager = layoutManager

        rvArticle.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                LinearLayoutManager.VERTICAL
            )
        )

        adapter = ArticleAdapter(
            articles,
            requireActivity().getSharedPreferences(
                requireActivity().packageName + "_preferences",
                Context.MODE_PRIVATE
            ),
            this
        )
        rvArticle.adapter = adapter

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(rvArticle)

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

        swipeRefreshLayout.setOnRefreshListener {
            currentPage = 1
            articles.clear()
            adapter?.notifyDataSetChanged()
            loadNewsData()
        }

        buttonRetry.setOnClickListener {
            loadNewsData()
        }

        arguments?.getString("category")?.let { category ->
            this.category = category
        }

        loadNewsData()
    }

    private fun loadNewsData() {
        progressBar.visibility = View.VISIBLE

        val networkUtils = NetworkUtils(requireContext())

        if (networkUtils.isInternetConnected()) {
            val call: Call<ResNews> =
                RetrofitClient.apiService.getTopHeadlines(country, category, currentPage)
            call.enqueue(object : Callback<ResNews> {
                override fun onResponse(call: Call<ResNews>, response: Response<ResNews>) {
                    progressBar.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                    if (response.isSuccessful) {
                        val newsResponse: ResNews? = response.body()
                        val newArticles: List<Article>? = newsResponse?.articles

                        textMessage.text = newsResponse?.message

                        newArticles?.let {
                            if (it.isNotEmpty()) {
                                articles.addAll(it)
                                adapter?.notifyDataSetChanged()
                            } else {
                                isLastPage = true
                            }
                        }
                    } else {
                        val errorResponse: ResError? =
                            Gson().fromJson(
                                response.errorBody()?.charStream(),
                                ResError::class.java
                            )
                        if (errorResponse != null) {
                            textMessage.visibility = View.VISIBLE
                            textMessage.text = errorResponse.message
                        } else {
                            Log.e("API Response", "Error: ${response.code()}")
                        }
                    }

                    isLoading = false
                }

                override fun onFailure(call: Call<ResNews>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("API Call", "Failed: ${t.message}")
                    swipeRefreshLayout.isRefreshing = false
                    isLoading = false
                }
            })
            networkUtils.hideErrorLayout(binding)
        } else {
            networkUtils.showErrorLayout(binding)
            progressBar.visibility = View.GONE
        }
    }

    override fun onLoadMore() {
        isLoading = true
        currentPage++
        loadNewsData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ItemTouchHelperCallback : ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // Tidak perlu implementasi karena tidak menggunakan drag and drop
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val article = adapter.articles[position]

            val scope = CoroutineScope(Dispatchers.IO)
            // Simpan artikel ke dalam Room database
            scope.launch {
                // Akses instance Room database
                val database = AppDatabase.getInstance(requireActivity()).articleDao()

                if (database.getAllArticles().contains(article)) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "The article has been saved",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Panggil metode untuk menyimpan artikel ke dalam database
                    database.insert(article)

                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "The article has been successfully saved",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            // Refresh tampilan RecyclerView
            adapter.notifyDataSetChanged()
        }
    }
}

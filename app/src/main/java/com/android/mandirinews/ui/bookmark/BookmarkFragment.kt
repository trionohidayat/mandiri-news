package com.android.mandirinews.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.mandirinews.database.ArticleDatabase
import com.android.mandirinews.Article
import com.android.mandirinews.adapter.SearchAdapter
import com.android.mandirinews.databinding.FragmentBookmarkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var rvArticle: RecyclerView

    private lateinit var adapter: SearchAdapter
    private val articles: MutableList<Article> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = binding.swipeRefreshLayout
        rvArticle = binding.rvArticle

        val layoutManager = LinearLayoutManager(requireContext())
        rvArticle.layoutManager = layoutManager

        rvArticle.addItemDecoration(
            DividerItemDecoration(
                requireActivity(), LinearLayoutManager.VERTICAL
            )
        )

        adapter = SearchAdapter(articles)
        rvArticle.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            articles.clear()
            adapter?.notifyDataSetChanged()
            loadNewsData()
        }

        loadNewsData()
    }

    private fun loadNewsData() {
        articles.clear()

        val database = ArticleDatabase.getInstance(requireContext())

        GlobalScope.launch(Dispatchers.IO) {
            val bookmarkedArticles = database.articleDao().getAllArticles()

            articles.addAll(bookmarkedArticles)

            launch(Dispatchers.Main) {
                adapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}
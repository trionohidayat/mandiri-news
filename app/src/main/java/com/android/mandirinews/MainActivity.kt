package com.android.mandirinews

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.mandirinews.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.search.SearchView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerSearch: RecyclerView
    private lateinit var viewPager: ViewPager2

    private val hintList = listOf("Mandiri News", "Bitcoin", "Ukraine", "COVID", "Nasa", "Reddit")
    private var hintIndex = 0
    private lateinit var hintChangeHandler: Handler

    private val categoryList =
        listOf("Business", "Entertainment", "Health", "Science", "Sports", "Technology")

    private var adapter: SearchAdapter? = null
    private val articles: MutableList<Article> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBarLayout = binding.appBar
        toolbar = binding.toolbar
        tabLayout = binding.tabLayout
        searchView = binding.searchView
        progressBar = binding.progressBar
        recyclerSearch = binding.recyclerSearch
        viewPager = binding.viewPager

        setSupportActionBar(toolbar)

        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            performSearch(searchView.editText.text.toString())
//            searchView.hide()
            false
        }

        startHintChangeTimer()

        setupRecyclerView()

        val fragmentAdapter = FragmentAdapter(categoryList, this)
        viewPager.adapter = fragmentAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.round_whatshot_24)
                }

                else -> {
                    tab.text = categoryList[position]
                }
            }
        }.attach()

        viewPager.isUserInputEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                searchView.show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerSearch.layoutManager = layoutManager

        recyclerSearch.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        adapter = SearchAdapter(articles)
        recyclerSearch.adapter = adapter
    }

    private fun startHintChangeTimer() {
        hintChangeHandler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                hintIndex = (hintIndex + 1) % hintList.size
                setSearchHint()
                hintChangeHandler.postDelayed(
                    this,
                    5000
                )
            }
        }
        hintChangeHandler.postDelayed(
            runnable,
            5000
        )
    }

    private fun setSearchHint() {
        searchView.hint = hintList[hintIndex]
    }

    private fun performSearch(query: String) {
        progressBar.visibility = View.VISIBLE
        recyclerSearch.visibility = View.INVISIBLE

        val call: Call<ResNews> =
            RetrofitClient.apiService.getEverything(query)
        call.enqueue(object : Callback<ResNews> {
            override fun onResponse(call: Call<ResNews>, response: Response<ResNews>) {
                progressBar.visibility = View.GONE
                recyclerSearch.visibility = View.VISIBLE
                if (response.isSuccessful) {
                    val newsResponse: ResNews? = response.body()
                    val newArticles: List<Article>? = newsResponse?.articles

                    newArticles?.let {
                        articles.clear()
                        articles.addAll(it)
                        adapter?.notifyDataSetChanged()
                    }
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
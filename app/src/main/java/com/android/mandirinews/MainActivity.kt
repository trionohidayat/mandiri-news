package com.android.mandirinews

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mandirinews.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.search.SearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerSearch: RecyclerView

    private val hintList = listOf("Mandiri News", "Bitcoin", "Ukraine", "COVID", "Nasa", "Reddit")
    private var hintIndex = 0
    private lateinit var hintChangeHandler: Handler

    private var adapter: SearchAdapter? = null
    private val articles: MutableList<Article> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_bookmark
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        searchView = binding.appBarMain.searchView
        progressBar = binding.appBarMain.progressBar
        recyclerSearch = binding.appBarMain.recyclerSearch

        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            performSearch(searchView.editText.text.toString())
            false
        }

        startHintChangeTimer()

        setupRecyclerView()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                searchView.show()
                true
            }

            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
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
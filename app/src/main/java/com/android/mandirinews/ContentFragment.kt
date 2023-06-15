package com.android.mandirinews

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
import com.android.mandirinews.databinding.FragmentContentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContentFragment : Fragment() {
    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private var data: String? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var rvArticle: RecyclerView

    private val country = "us"
    var category = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar
        rvArticle = binding.rvArticle

        rvArticle.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL))

        arguments?.getString("category")?.let { category ->
            this.category = category
        }

        loadNewsData()

        data?.let {
            // Tampilkan data di fragment sesuai kebutuhan
        }
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
                    rvArticle.layoutManager = LinearLayoutManager(requireActivity())
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

    fun setData(data: String) {
        this.data = data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

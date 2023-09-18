package com.android.mandirinews.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.mandirinews.R
import com.android.mandirinews.adapter.FragmentAdapter
import com.android.mandirinews.databinding.FragmentHomeBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val categoryList =
        listOf("Business", "Entertainment", "Health", "Science", "Sports", "Technology")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout = binding.appBar
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager

        val fragmentAdapter = FragmentAdapter(categoryList, requireActivity())
        viewPager.adapter = fragmentAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(requireContext(), R.drawable.round_whatshot_24)
                }

                else -> {
                    tab.text = categoryList[position]
                }
            }
        }.attach()

        viewPager.isUserInputEnabled = false
    }
}
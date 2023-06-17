package com.android.mandirinews

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(
    private val data: List<String>,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = data.size

    override fun createFragment(position: Int): Fragment {
        val fragment = ContentFragment()
        val bundle = Bundle()

        if (position == 0) {
            bundle.putString("category", "")
        } else {
            bundle.putString("category", data[position])
        }

        fragment.arguments = bundle
        return fragment
    }
}

package com.android.mandirinews

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import com.android.mandirinews.databinding.FragmentContentBinding

class NetworkUtils(private val context: Context) {

    fun isInternetConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun showErrorLayout(binding: FragmentContentBinding) {
        binding.includeError.layoutError.visibility = View.VISIBLE
        binding.includeError.textStatus.text = "No Internet Connection"
        binding.includeError.textError.text = "Please check your internet connection and try again."
    }

    fun hideErrorLayout(binding: FragmentContentBinding) {
        binding.includeError.layoutError.visibility = View.GONE
    }
}

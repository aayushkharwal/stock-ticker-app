package com.example.tickerappxml

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChartsTabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val ticker: String,
    private val changeColor: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putString("ticker", ticker)
        bundle.putString("changeColor", changeColor)
        return if (position == 0)
            PriceChartFragment().also { it.arguments = bundle }
        else
            SMAChartFragment().also { it.arguments = bundle }
    }
}
package com.example.tickerappxml

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tickerappxml.databinding.WatchlistItemBinding
import models.Watchlist
import java.math.BigDecimal
import java.math.RoundingMode


class WatchlistAdapter(
    var context: Context,
    var watchlistItems: MutableList<Watchlist>
) : RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>() {

    inner class WatchlistViewHolder(val binding: WatchlistItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = WatchlistItemBinding.inflate(layoutInflater, parent, false)

        return WatchlistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return watchlistItems.size
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        holder.binding.apply {
            ticker.text = watchlistItems[position].ticker
            name.text =  watchlistItems[position].name
            currentPrice.text = "$${BigDecimal(watchlistItems[position].c).setScale(2, RoundingMode.HALF_EVEN).toString()}"
            priceChange.text = "$${BigDecimal(watchlistItems[position].d).setScale(2, RoundingMode.HALF_EVEN).toString()} (${BigDecimal(watchlistItems[position].dp).setScale(2, RoundingMode.HALF_EVEN).toString()}%)"
            if (watchlistItems[position].d > 0) {
                priceChangeIcon.setImageResource(R.drawable.trending_up)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(priceChangeIcon.getDrawable()),
                    ContextCompat.getColor(context, R.color.green)
                )
                priceChange.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else if (watchlistItems[position].d < 0) {
                priceChangeIcon.setImageResource(R.drawable.trending_down)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(priceChangeIcon.getDrawable()),
                    ContextCompat.getColor(context, R.color.red)
                )
                priceChange.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
        }
        Log.d("PortfolioBind", "Position: $position, Ticker: ${watchlistItems[position].ticker}")

        holder.binding.toStock.setOnClickListener {
            Log.d("PortfolioBind", "Position: $position, Ticker: ${watchlistItems[position].ticker}")

            val bundle = Bundle()
            bundle.putString("ticker", watchlistItems[position].ticker)

            it.findNavController().navigate(R.id.action_homeFragment_to_stockInfoFragment, bundle)
        }
    }

//    fun deleteItem(i: Int){
//        watchlistItems.removeAt(i)
//        notifyItemRemoved(i)
//    }


}
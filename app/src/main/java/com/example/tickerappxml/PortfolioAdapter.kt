package com.example.tickerappxml

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tickerappxml.databinding.PortfolioItemBinding
import models.Portfolio
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioAdapter(
    var context: Context,
    var portfolioItems: MutableList<Portfolio>
) : RecyclerView.Adapter<PortfolioAdapter.PortfolioViewHolder>() {

    inner class PortfolioViewHolder(val binding: PortfolioItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfolioViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = PortfolioItemBinding.inflate(layoutInflater, parent, false)

        return PortfolioViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return portfolioItems.size
    }

    override fun onBindViewHolder(holder: PortfolioViewHolder, position: Int) {
        holder.binding.apply {

            var worthChange: Double
            worthChange = ((portfolioItems[position].c - (portfolioItems[position].totalCost/portfolioItems[position].quantity))/portfolioItems[position].quantity)

            ticker.text = portfolioItems[position].tickerSymbol
            owned.text = "${portfolioItems[position].quantity.toString()} shares"
            marketValue.text = "$${BigDecimal((portfolioItems[position].quantity * portfolioItems[position].c)).setScale(2, RoundingMode.HALF_EVEN).toString()}"
            change.text = "$${BigDecimal(worthChange).setScale(2, RoundingMode.HALF_EVEN).toString()} (${BigDecimal(worthChange*100/portfolioItems[position].totalCost).setScale(2, RoundingMode.HALF_EVEN).toString()}%)"

            if (worthChange > 0) {
                changeIcon.setImageResource(R.drawable.trending_up)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(changeIcon.getDrawable()),
                    ContextCompat.getColor(context, R.color.green)
                )
                change.setTextColor(ContextCompat.getColor(context, R.color.green))
            } else if (worthChange < 0) {
                changeIcon.setImageResource(R.drawable.trending_down)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(changeIcon.getDrawable()),
                    ContextCompat.getColor(context, R.color.red)
                )
                change.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                changeIcon.setImageResource(android.R.color.transparent)
                change.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

        }

        holder.binding.toStock2.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("ticker", portfolioItems[position].tickerSymbol)

            it.findNavController().navigate(R.id.action_homeFragment_to_stockInfoFragment, bundle)
        }
    }
}

package com.example.tickerappxml

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.tickerappxml.databinding.NewsItemBinding
import models.News
import models.Watchlist
import java.time.Duration
import java.time.Instant


class NewsAdapter(
    var context: Context,
    var newsItems: List<News>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = NewsItemBinding.inflate(layoutInflater, parent, false)

        return NewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsItems.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(context).load(newsItems[position].image).into(newsImage)
            headline.text = newsItems[position].headline
            source.text = newsItems[position].source
            newsTime.text = "${Duration.between(Instant.ofEpochSecond(newsItems[position].datetime_unix), Instant.now()).toHours()} hours"
        }
        holder.itemView.setOnClickListener {
            // Open dialog with item details
            NewsDialog(context, newsItems[position]).show()
        }
    }

}
package com.example.tickerappxml

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tickerappxml.databinding.NewsItemBinding
import com.example.tickerappxml.databinding.PeersItemBinding
import models.News

class PeersAdapter(
    var context: Context,
    var peers: List<String>
) : RecyclerView.Adapter<PeersAdapter.PeersViewHolder>(){

    inner class PeersViewHolder(val binding: PeersItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PeersAdapter.PeersViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = PeersItemBinding.inflate(layoutInflater, parent, false)
        return PeersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeersAdapter.PeersViewHolder, position: Int) {

        holder.binding.apply {
            var content = SpannableString(peers[position])
            content.setSpan(UnderlineSpan(), 0, peers[position].length, 0)
            peer.text = content
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("ticker", peers[position])
            bundle.putBoolean("directedFromPeer", true)
            it.findNavController().navigate(R.id.action_stockInfoFragment_self, bundle)
        }

    }

    override fun getItemCount(): Int {
        return peers.size
    }
}
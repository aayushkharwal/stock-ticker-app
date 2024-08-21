package com.example.tickerappxml

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.marginStart
import com.example.tickerappxml.databinding.NewsDialogBinding
import models.News
import java.net.URLEncoder

class NewsDialog(context: Context, private val item: News) : Dialog(context) {

    private lateinit var binding: NewsDialogBinding

    override fun onStart() {
        super.onStart()
        window?.apply {
            // Ensure the dialog is as wide as the parent
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Apply a transparent background to allow custom rounded corners
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setLayout(context.resources.displayMetrics.widthPixels - 100 * 2, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewsDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newsCardSource.text = item.source
        binding.newsCardDatetime.text = item.datetime
        binding.newsCardHeadline.text = item.headline
        binding.newsCardDescription.text = item.summary


        // Button to open URL in Chrome (or any other browser)
        binding.newsCardChrome.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
            context.startActivity(browserIntent)
        }

        // Button to share URL on Twitter
        binding.newsCardTwitter.setOnClickListener {
            val tweetUrl = String.format(
                "https://twitter.com/intent/tweet?text=%s&url=%s",
                URLEncoder.encode("Check out this link:", "UTF-8"),
                URLEncoder.encode(item.url, "UTF-8")
            )
            val tweetIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))
            context.startActivity(tweetIntent)
        }

        // Button to share URL on Facebook
        binding.newsCardFacebook.setOnClickListener {
            val facebookUrl = String.format(
                "https://www.facebook.com/sharer/sharer.php?u=%s",
                URLEncoder.encode(item.url, "UTF-8")
            )
            val facebookIntent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
            context.startActivity(facebookIntent)
        }

    }
}

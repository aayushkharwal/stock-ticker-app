package com.example.tickerappxml

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.tickerappxml.databinding.FragmentHomeBinding
import com.example.tickerappxml.databinding.FragmentStockBinding
import com.google.android.material.tabs.TabLayoutMediator
import models.News
import models.Portfolio


val tabTitles = arrayOf(
    R.drawable.chart_line,
    R.drawable.clock_time_three
)

class StockFragment : Fragment() {

    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ticker =  requireArguments().getString("ticker");
        val changeColor = "red"
        _binding = FragmentStockBinding.inflate(inflater, container, false)


        val profileUrl = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_profile"
        val profileJsonObjectRequest = JsonObjectRequest(Request.Method.GET, profileUrl, null,
            { response ->
                Log.d("Profile Fetch Response", response.toString())
                binding.ticker.text = response.getString("ticker")
                binding.name.text = response.getString("name")
            },
            { error ->
                Log.d("Profile Fetch Error", error.message.toString())
            }
        )
        MySingleton.getInstance(requireContext()).addToRequestQueue(profileJsonObjectRequest)



        setupWebView(binding.earningsChartWebView, "drawEarningsChart('${ticker}')")
        setupWebView(binding.recommendationChartWebView, "drawRecommendationChart('${ticker}')")

        val adapter = ChartsTabAdapter(requireActivity().getSupportFragmentManager(), lifecycle,
            ticker!!, changeColor)
        binding.chartsTabViewPager2.adapter = adapter

        TabLayoutMediator(binding.chartsTab, binding.chartsTabViewPager2) { tab, position ->
            tab.icon = ContextCompat.getDrawable(requireContext(), tabTitles[position])
        }.attach()



        val newsItems = mutableListOf<News>()
        val newsUrl = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_news"

        val newsJsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, newsUrl, null,
            { response ->
                Log.d("News Fetch Response", response.toString())

                for (i in 0 until response.length()) {
                    val jsonObj = response.getJSONObject(i)
                    newsItems.add(
                        News(
                            jsonObj.getLong("datetime_unix"),
                            jsonObj.getString("datetime"),
                            jsonObj.getString("headline"),
                            jsonObj.getString("image"),
                            jsonObj.getString("url"),
                            jsonObj.getString("category"),
                            jsonObj.getString("source"),
                            jsonObj.getString("summary"),
                            jsonObj.getString("related"),
                            jsonObj.getInt("id")

                        )
                    )

                }

                Log.d("News Fetch Response 2", newsItems.toString())

                val newsAdapter = NewsAdapter(requireContext(), newsItems)
                binding.newsRecyclerView.adapter = newsAdapter
                binding.newsRecyclerView.layoutManager = LinearLayoutManager(context)

            },
            { error ->
                Log.d("News Fetch Error", error.message.toString())
            }
        )

        MySingleton.getInstance(requireContext()).addToRequestQueue(newsJsonObjectRequest)


        return binding.root
    }

    private fun setupWebView(webView: WebView, jsFunction: String) {
        webView.settings.javaScriptEnabled = true
//        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("file:///android_asset/charts.html")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript(jsFunction, null)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }
}

//old
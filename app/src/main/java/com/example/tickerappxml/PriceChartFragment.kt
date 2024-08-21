package com.example.tickerappxml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.tickerappxml.databinding.FragmentHomeBinding
import com.example.tickerappxml.databinding.FragmentPriceChartBinding
import com.example.tickerappxml.databinding.FragmentStockBinding

class PriceChartFragment : Fragment() {

    private var _binding: FragmentPriceChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPriceChartBinding.inflate(inflater, container, false)
        val webView: WebView = binding.priceChartWebView
        val ticker = arguments?.getString("ticker")
        val changeColor = arguments?.getString("changeColor")

        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/charts.html")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript("drawPriceChart('${ticker}', '${changeColor}')", null)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }


}
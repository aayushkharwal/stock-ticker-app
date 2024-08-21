package com.example.tickerappxml

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.tickerappxml.databinding.FragmentPriceChartBinding
import com.example.tickerappxml.databinding.FragmentSmaChartBinding

class SMAChartFragment : Fragment() {

    private var _binding: FragmentSmaChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSmaChartBinding.inflate(inflater, container, false)
        val webView: WebView = binding.smaChartWebView
        val ticker = arguments?.getString("ticker")


        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/charts.html")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript("drawSMAChart('${ticker}')", null)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }

}
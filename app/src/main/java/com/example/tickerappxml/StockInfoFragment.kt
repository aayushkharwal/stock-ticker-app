package com.example.tickerappxml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tickerappxml.databinding.FragmentStockInfoBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.plcoding.animatedsplashscreen.StockViewModel
import models.News
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant

val tabTitles2 = arrayOf(
    R.drawable.chart_line,
    R.drawable.clock_time_three
)

class StockInfoFragment : Fragment() {

    private var _binding: FragmentStockInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by viewModels()

    private lateinit var nameCompany: String
    private lateinit var firstNews: News

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockInfoBinding.inflate(inflater, container, false)

        val ticker = requireArguments().getString("ticker")!!


        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        val fetchAll = savedStateHandle?.get<Boolean>("fetchAll")
        savedStateHandle?.remove<Boolean>("fetchAll")

//        viewModel.loadStockProfile(ticker)
//        viewModel.loadStockNews(ticker)
//        viewModel.loadStockSentiments(ticker)
//        viewModel.loadStockQuote(ticker)
        if (fetchAll == false) {
            Log.d("Not fetching data", fetchAll.toString())
        } else {
            viewModel.loadStockPage(ticker)
        }


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.stockPage.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.stockPage.visibility = View.VISIBLE
            }
        }

//        viewModel.portfolioQuote.observe(viewLifecycleOwner) { marketQuote ->
//            binding.portfolioChange.text = "$${BigDecimal(marketQuote.marketChange).setScale(2, RoundingMode.HALF_EVEN).toString()}"
//            binding.marketValue.text = "$${BigDecimal(marketQuote.marketValue).setScale(2, RoundingMode.HALF_EVEN).toString()}"
//        }
//
//        viewModel.stockState.observe(viewLifecycleOwner) { state ->
//            binding.owned.text = "${state.portfolio.quantity.toString()}"
//            binding.avgCostShare.text = "$${BigDecimal((state.portfolio.totalCost/state.portfolio.quantity)).setScale(2, RoundingMode.HALF_EVEN).toString()}"
//            binding.totalCost.text = "$${BigDecimal(state.portfolio.totalCost).setScale(2, RoundingMode.HALF_EVEN).toString()}"
//
//
//            // Transaction Button
//            binding.trade.setOnClickListener {
//                // Open dialog with item details
//                TransactionFragment(requireContext()).show()
//            }
//
//        }

        viewModel.transactionDetails3.observe(viewLifecycleOwner) { transactionData ->

                val changeValue = (transactionData.totalCost - (transactionData.currentPrice * transactionData.quantity))
                binding.owned.text = transactionData.quantity.toString()

                binding.marketValue.text = "$${BigDecimal((transactionData.currentPrice * transactionData.quantity)).setScale(2, RoundingMode.HALF_EVEN).toString()}"
                binding.portfolioChange.text = "$${BigDecimal(changeValue).setScale(2, RoundingMode.HALF_EVEN).toString()}"

                if (changeValue<0) {
                    binding.marketValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.portfolioChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else if (changeValue>0) {
                    binding.marketValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.portfolioChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                } else {
                    binding.marketValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    binding.portfolioChange.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }

            if (transactionData.quantity!=0) {
                binding.avgCostShare.text = "$${BigDecimal((transactionData.totalCost/transactionData.quantity)).setScale(2, RoundingMode.HALF_EVEN).toString()}"
                binding.totalCost.text = "$${BigDecimal(transactionData.totalCost).setScale(2, RoundingMode.HALF_EVEN).toString()}"

            } else {
                binding.avgCostShare.text = "$0.00"
                binding.totalCost.text = "$0.00"

            }
            // Transaction Button
            binding.trade.setOnClickListener {
                // Open dialog with item details
                TransactionFragment(requireContext(), viewModel, transactionData).show()
            }

        }


        viewModel.stockProfile.observe(viewLifecycleOwner) { profile ->
            // Update UI with stock profile data
            binding.ticker.text = profile.ticker
            binding.name.text = profile.name
            binding.ipo.text = profile.ipo
            binding.industry.text = profile.finnhubIndustry
            binding.webpage.text = profile.weburl

//            binding.webpage.setOnClickListener {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(profile.weburl))
//                context?.startActivity(browserIntent)
//            }

            binding.tickerSentiments.text = profile.name

            nameCompany = profile.name
        }

        viewModel.stockQuote.observe(viewLifecycleOwner) { quote ->
            // Update UI with stock profile data
            var trackColorChange: String = "black"
            binding.currentPrice.text = "$${BigDecimal(quote.c).setScale(2, RoundingMode.HALF_EVEN).toString()}"
            binding.change.text = "$${BigDecimal(quote.d).setScale(2, RoundingMode.HALF_EVEN).toString()} (${BigDecimal(quote.dp).setScale(2, RoundingMode.HALF_EVEN).toString()}%)"
            if (quote.d > 0) {
                binding.changeIcon.setImageResource(R.drawable.trending_up)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(binding.changeIcon.getDrawable()),
                    ContextCompat.getColor(requireContext(), R.color.green)
                )
                binding.change.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                trackColorChange = "green"
            } else if (quote.d < 0) {
                binding.changeIcon.setImageResource(R.drawable.trending_down)
                DrawableCompat.setTint(
                    DrawableCompat.wrap(binding.changeIcon.getDrawable()),
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
                binding.change.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                trackColorChange = "red"
            }

            binding.openPrice.text = "$" + BigDecimal(quote.o).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.highPrice.text = "$" + BigDecimal(quote.h).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.prevClose.text = "$" + BigDecimal(quote.pc).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.lowPrice.text = "$" + BigDecimal(quote.l).setScale(2, RoundingMode.HALF_EVEN).toString()

            // Set up tabs and ViewPager
            setupTabs(ticker, trackColorChange)

        }

        viewModel.stockSentiments.observe(viewLifecycleOwner) { sentiments ->
            // Update UI with stock profile data
            binding.msrpTotal.text = BigDecimal(sentiments.mspr_total).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.changeTotal.text = BigDecimal(sentiments.change_total).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.msrpPositive.text = BigDecimal(sentiments.mspr_positive).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.changePositive.text = BigDecimal(sentiments.change_positive).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.msrpNegative.text = BigDecimal(sentiments.mspr_negative).setScale(2, RoundingMode.HALF_EVEN).toString()
            binding.changeNegative.text = BigDecimal(sentiments.change_negative).setScale(2, RoundingMode.HALF_EVEN).toString()
        }

        viewModel.stockPeers.observe(viewLifecycleOwner) { peers ->
            // Update UI with news data
            setupPeersRecyclerView(peers)
        }

        viewModel.newsItems.observe(viewLifecycleOwner) { news ->
            // Update UI with news data
            firstNews = news[0]
            Glide.with(requireContext()).load(news[0].image).into(binding.firstNewsImage)
            binding.firstNewsHeadline.text = news[0].headline
            binding.firstNewsSource.text = news[0].source
            binding.firstNewsTime.text = "${Duration.between(Instant.ofEpochSecond(news[0].datetime_unix), Instant.now()).toHours()} hours"
            setupNewsRecyclerView(news.drop(1))
        }

        binding.firstNewsCard.setOnClickListener {
            // Open dialog with item details
            NewsDialog(requireContext(), firstNews).show()
        }

        viewModel.error.observe(viewLifecycleOwner, { error ->
            Log.d("Error while fetching data", error)
        })

        setupWebView(binding.earningsChartWebView, "drawEarningsChart('$ticker')")
        setupWebView(binding.recommendationChartWebView, "drawRecommendationChart('$ticker')")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val ticker = arguments?.getString("ticker")!!
        val searched = arguments?.getBoolean("searched")
        val directedFromPeer = arguments?.getBoolean("directedFromPeer")


        val actionBar: Toolbar = requireActivity().findViewById(R.id.actionBar)
        val actionBarView = LayoutInflater.from(requireContext()).inflate(R.layout.action_bar_stock, actionBar, false)

        actionBar.removeAllViews()
        actionBar.addView(actionBarView)


        actionBarView.findViewById<TextView>(R.id.stockTitle).text = ticker
        actionBarView.findViewById<ImageButton>(R.id.backButtonStock).setOnClickListener {
            if (directedFromPeer == true) {
                Log.d("AAAAA", directedFromPeer.toString())
//                findNavController().popBackStack()
                findNavController().apply {
                    previousBackStackEntry?.savedStateHandle?.set("fetchAll", false)
                    popBackStack()
                }
            } else {
                val bundle = Bundle()
                if (searched == true) {
                    bundle.putString("searchedTicker", ticker)
                }
                findNavController().navigate(R.id.action_stockInfoFragment_to_homeFragment, bundle)
            }
        }

        val favoriteButton = actionBarView.findViewById<ImageButton>(R.id.favoriteButton)
        viewModel.stockState.observe(viewLifecycleOwner) { state ->
            if (state.watchlist.status) {
                favoriteButton.setImageResource(R.drawable.star)
            } else {
                favoriteButton.setImageResource(R.drawable.star_outline)
            }
        }

        favoriteButton.setOnClickListener {
            viewModel.updateFavorite(ticker, nameCompany)
        }

        viewModel.favoriteStatus.observe(viewLifecycleOwner) { state ->
            if (state.status == "added to") {
                favoriteButton.setImageResource(R.drawable.star)
            } else {
                favoriteButton.setImageResource(R.drawable.star_outline)
            }
            Toast.makeText(requireContext(), "${ticker} ${state.status} favorites", Toast.LENGTH_LONG).show()
        }

        viewModel.loadStockState(ticker)


    }


    private fun setupWebView(webView: WebView, jsFunction: String) {
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/charts.html")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                webView.evaluateJavascript(jsFunction, null)
            }
        }
    }

    private fun setupNewsRecyclerView(newsItems: List<News>) {
        val newsAdapter = NewsAdapter(requireContext(), newsItems)
        binding.newsRecyclerView.adapter = newsAdapter
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupPeersRecyclerView(peers: List<String>) {
        val peersAdapter = PeersAdapter(requireContext(), peers)
        binding.peersRecyclerView.adapter = peersAdapter
        binding.peersRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupTabs(ticker: String, trackColorChange: String) {
        val adapter = ChartsTabAdapter(requireActivity().supportFragmentManager, lifecycle, ticker, trackColorChange)
        binding.chartsTabViewPager2.adapter = adapter
        TabLayoutMediator(binding.chartsTab, binding.chartsTabViewPager2) { tab, position ->
            tab.icon = ContextCompat.getDrawable(requireContext(), tabTitles2[position])
        }.attach()

        binding.chartsTab.getTabAt(binding.chartsTab.selectedTabPosition)?.icon?.setTint(
            ContextCompat.getColor(requireContext(), R.color.blue)
        )

        // Add a TabSelectedListener to change the icon color on tab selection
        binding.chartsTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon?.setTint(ContextCompat.getColor(requireContext(), R.color.blue))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.d("No tab re-selection, do nothing", false.toString())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }
}

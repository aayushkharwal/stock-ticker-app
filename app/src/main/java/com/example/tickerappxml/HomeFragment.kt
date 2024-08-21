package com.example.tickerappxml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tickerappxml.databinding.FragmentHomeBinding
import com.plcoding.animatedsplashscreen.StockViewModel
import models.Portfolio
import models.Watchlist
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StockViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        binding.date.text = formatter.format(Date())

        viewModel.walletData.observe(viewLifecycleOwner) { wallet ->
            binding.balance.text = "$${BigDecimal(wallet.balance).setScale(2, RoundingMode.HALF_EVEN).toString()}"
//            viewModel.calculateWorth()
        }

        viewModel.portfolioItems.observe(viewLifecycleOwner) { portfolioItems ->
            setupPortfolioRecyclerView(portfolioItems)
//            viewModel.calculateWorth()
        }

        viewModel.netWorth.observe(viewLifecycleOwner) { worth ->
            binding.netWorth.text = "$${BigDecimal(worth).setScale(2, RoundingMode.HALF_EVEN).toString()}"
        }

        viewModel.watchlistItems.observe(viewLifecycleOwner) { watchlistItems ->
            setupWatchlistRecyclerView(watchlistItems)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Log.d("HomeFragment Error", message)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.homePage.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.homePage.visibility = View.VISIBLE
            }
        }

//        viewModel.loadWallet()
//        viewModel.loadPortfolio()
//        viewModel.loadWatchlist()
        viewModel.loadHomePage()

        setupClickListeners()

        return binding.root
    }


    private fun setupWatchlistRecyclerView(items: MutableList<Watchlist>) {
        val adapter = WatchlistAdapter(requireContext(), items)
        binding.watchlistRecyclerView.adapter = adapter
        binding.watchlistRecyclerView.layoutManager = LinearLayoutManager(context)

        val swipeGestures = object : SwipeGestures(requireActivity()) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from_pos = viewHolder.bindingAdapterPosition
                val to_pos = target.bindingAdapterPosition
                Collections.swap(items, from_pos, to_pos)
                adapter.notifyItemMoved(from_pos, to_pos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        val position = viewHolder.getBindingAdapterPosition()
                        val deletedItem: Watchlist = items.get(position)
                        items.removeAt(position)
                        adapter.notifyItemRemoved(position)

                        Toast.makeText(requireContext(), "${deletedItem.ticker} removed from Favorites", Toast.LENGTH_LONG).show()

                        viewModel.favoriteStatus.observe(viewLifecycleOwner) { favoriteResponse ->
//                            Toast.makeText(requireContext(), "${deletedItem.ticker} ${favoriteResponse.status} Favorites", Toast.LENGTH_LONG).show()
                        }

                        viewModel.updateFavorite(deletedItem.ticker, deletedItem.name)
                    }
                }
            }

        }

        val touchHelper = ItemTouchHelper(swipeGestures)
        touchHelper.attachToRecyclerView(binding.watchlistRecyclerView)

    }

    private fun setupPortfolioRecyclerView(items: List<Portfolio>) {
        val adapter = PortfolioAdapter(requireContext(), items.toMutableList())
        binding.portfolioRecyclerView.adapter = adapter
        binding.portfolioRecyclerView.layoutManager = LinearLayoutManager(context)


        val swipeGestures = object : SwipeGestures(requireActivity()) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from_pos = viewHolder.bindingAdapterPosition
                val to_pos = target.bindingAdapterPosition
                Collections.swap(items, from_pos, to_pos)
                adapter.notifyItemMoved(from_pos, to_pos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }

        val touchHelper = ItemTouchHelper(swipeGestures)
        touchHelper.attachToRecyclerView(binding.portfolioRecyclerView)

    }

    private fun setupClickListeners() {
        binding.finnhub.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.finnhub.io/"))
            context?.startActivity(browserIntent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionBar: Toolbar = requireActivity().findViewById(R.id.actionBar)

        val actionBarInitial = LayoutInflater.from(requireContext()).inflate(R.layout.action_bar_home_initial, actionBar, false)
        val actionBarEditable = LayoutInflater.from(requireContext()).inflate(R.layout.action_bar_home_editable, actionBar, false)

        actionBarInitial.findViewById<ImageButton>(R.id.searchButton).setOnClickListener {
            actionBar.removeAllViews()  // Remove previous views in the toolbar (if any)
            actionBar.addView(actionBarEditable)
        }

        val backButton = actionBarEditable.findViewById<ImageButton>(R.id.backButton)
        val searchInput = actionBarEditable.findViewById<AutoCompleteTextView>(R.id.searchInput)
        val clearButton = actionBarEditable.findViewById<ImageButton>(R.id.clearButton)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Before TextChange: ", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("On TextChange: ", s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
                Log.d("After TextChange: ", s.toString())
                if (!(s.isNullOrEmpty()) && s.toString().length >= 1) {
                    clearButton.visibility = View.VISIBLE
                    viewModel.loadAutoComplete(s.toString())
                }
            }
        })


        backButton.setOnClickListener {
            actionBar.removeAllViews()  // Remove previous views in the toolbar (if any)
            actionBar.addView(actionBarInitial)
        }

        clearButton.setOnClickListener {
            searchInput.text.clear()
            clearButton.visibility = View.GONE
        }

        val searchedTicker = arguments?.getString("searchedTicker")
        if (searchedTicker!=null){
            actionBar.removeAllViews()  // Remove previous views in the toolbar (if any)
            actionBar.addView(actionBarEditable)
            searchInput.setText(searchedTicker)
            clearButton.visibility = View.VISIBLE
        } else {
            actionBar.removeAllViews()  // Remove previous views in the toolbar (if any)
            actionBar.addView(actionBarInitial)
        }

        viewModel.autoCompleteItems.observe(viewLifecycleOwner) { autoComplete ->
            val suggestionsList = ArrayList<String>()
            for (i in 0 until autoComplete.size) {
                val text = "${autoComplete[i].symbol} | ${autoComplete[i].description}"
                suggestionsList.add(text)
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestionsList)
            searchInput.setAdapter(adapter)
            searchInput.setOnItemClickListener { _, _, position, _ ->
                searchInput.setText(autoComplete[position].symbol) // Set the text to the selected item

                val bundle = Bundle()
                bundle.putString("ticker", autoComplete[position].symbol)
                bundle.putBoolean("searched", true)
                findNavController().navigate(R.id.action_homeFragment_to_stockInfoFragment, bundle)
            }
            searchInput.showDropDown()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }
}

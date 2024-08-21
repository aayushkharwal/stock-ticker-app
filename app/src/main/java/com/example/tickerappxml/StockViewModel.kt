package com.plcoding.animatedsplashscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import models.AutoComplete
import models.Favorite
import models.News
import models.Portfolio
import models.Quote
import models.Sentiments
import models.StockProfile
import models.StockState
import models.TransactionDetails
import models.Wallet
import models.Watchlist
import repository.StockRepository

class StockViewModel: ViewModel() {

    private val stockRepository = StockRepository()

    // LiveData to hold worth data
    private val _netWorth = MutableLiveData<Double>()
    val netWorth: LiveData<Double> = _netWorth

//    private val _portfolioQuote = MutableLiveData<MarketQuote>()
//    val portfolioQuote: LiveData<MarketQuote> = _portfolioQuote

//    private val _transactionData = MutableLiveData<TransactionDetails>()
//    val transactionData: LiveData<TransactionDetails> = _transactionData

    // LiveData to hold loading data
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to hold stock profile data
    val stockProfile: MutableLiveData<StockProfile> by lazy {
        MutableLiveData<StockProfile>()
    }

    // LiveData to hold stock profile data
    val transactionDetails3: MutableLiveData<TransactionDetails> by lazy {
        MutableLiveData<TransactionDetails>()
    }

    // LiveData to hold stock quote data
    val stockQuote: MutableLiveData<Quote> by lazy {
        MutableLiveData<Quote>()
    }

    // LiveData to hold stock quote data
    val stockSentiments: MutableLiveData<Sentiments> by lazy {
        MutableLiveData<Sentiments>()
    }

    // LiveData to hold stock quote data
    val stockPeers: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    // LiveData to hold stock quote data
    val stockState: MutableLiveData<StockState> by lazy {
        MutableLiveData<StockState>()
    }

    // LiveData to hold news data
    val newsItems: MutableLiveData<List<News>> by lazy {
        MutableLiveData<List<News>>()
    }

    // LiveData to hold autocomplete data
    val autoCompleteItems: MutableLiveData<MutableList<AutoComplete>> by lazy {
        MutableLiveData<MutableList<AutoComplete>>()
    }

    // LiveData to wallet errors
    val walletData: MutableLiveData<Wallet> by lazy {
        MutableLiveData<Wallet>()
    }

    // LiveData to hold watchlist data
    val watchlistItems: MutableLiveData<MutableList<Watchlist>> by lazy {
        MutableLiveData<MutableList<Watchlist>>()
    }

    // LiveData to hold portfolio data
    val portfolioItems: MutableLiveData<List<Portfolio>> by lazy {
        MutableLiveData<List<Portfolio>>()
    }

    // LiveData to hold favorite data
    val favoriteStatus: MutableLiveData<Favorite> by lazy {
        MutableLiveData<Favorite>()
    }

    // LiveData to hold errors
    val error: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    init {
        // Initialize loading state to false
        _isLoading.value = false
    }

    fun updateFavorite(ticker: String, name: String) {
        viewModelScope.launch {
            try {
                val status = stockRepository.updateWatchlist(ticker, name)
                favoriteStatus.postValue(status)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

    }

    fun loadStockPage(ticker: String) {
        _isLoading.value = true
        Log.d("TESTTTT1", _isLoading.value.toString())
        viewModelScope.launch {
            try {
                val profile = async { stockRepository.fetchStockProfile(ticker) }
                val news = async { stockRepository.fetchStockNews(ticker) }
                val sentiments = async { stockRepository.fetchStockSentiment(ticker) }
                val peers = async { stockRepository.fetchStockPeers(ticker) }
                val stateData = async { stockRepository.fetchStockState(ticker) }
                val quote = async { stockRepository.fetchStockQuote2(ticker) }
                val wallet = async { stockRepository.fetchWallet() }

                // Wait for all requests to complete
                stockProfile.postValue(profile.await())
                newsItems.postValue(news.await())
                stockSentiments.postValue(sentiments.await())
                stockPeers.postValue(peers.await())
//                stockState.postValue(stateData.await())
                stockQuote.postValue(quote.await())

//                val marketQuote = MarketQuote(
//                    marketValue = quote.await().c * stateData.await().portfolio.quantity,
//                    marketChange = (stateData.await().portfolio.totalCost - (quote.await().c * stateData.await().portfolio.quantity))
//                )
//                _portfolioQuote.postValue(marketQuote)


                val stockTransaction = TransactionDetails(
                    quantity = stateData.await().portfolio.quantity,
                    totalCost = stateData.await().portfolio.totalCost,
                    ticker = profile.await().ticker,
                    name = profile.await().name,
                    currentPrice = quote.await().c,
                    balance = wallet.await().balance
                )
//                _transactionData.postValue(stockTransaction)
                transactionDetails3.postValue(stockTransaction)

                Log.d("TESTTTT2", _isLoading.value.toString())

                // Data loaded, update loading state
                _isLoading.postValue(false)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
                _isLoading.postValue(false)
            }
        }
    }


    fun executeTransaction(transactionDetails: TransactionDetails, action: String) {
        Log.d("BBB2", transactionDetails.toString())

        viewModelScope.launch {
            try {
                val transactionDetails2 = stockRepository.postTransaction(transactionDetails, action)
                transactionDetails3.postValue(transactionDetails2)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

//        stockRepository.postTransaction(transactionDetails, action) { isSuccess, transactionResponse ->
//            if (isSuccess) {
//
//                _transactionData.postValue(transactionResponse)
//                Log.d("Transaction Success: ", transactionResponse.toString())
//                // Post success handling, update LiveData or something else
//            } else {
//                Log.d("Transaction Failure: ", transactionResponse.toString())
//                // Handle failure
//            }
//        }
    }

    fun loadStockQuote(ticker: String) {

        stockRepository.fetchStockQuote(ticker, {
            stockQuote.postValue(it)
        }, {
            error.postValue(it)
        })

//        viewModelScope.launch {
//            try {
//                val quote = stockRepository.fetchStockQuote(ticker)
//                stockQuote.postValue(quote)
//            } catch (e: Exception) {
//                error.postValue(e.message ?: "Unknown error")
//            }
//        }
    }

    fun loadStockProfile(ticker: String) {
        viewModelScope.launch {
            try {
                val profile = stockRepository.fetchStockProfile(ticker)
                stockProfile.postValue(profile)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

    }

    fun loadStockSentiments(ticker: String) {
        viewModelScope.launch {
            try {
                val sentiments = stockRepository.fetchStockSentiment(ticker)
                stockSentiments.postValue(sentiments)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

    }

    fun loadStockNews(ticker: String) {
        viewModelScope.launch {
            try {
                val news = stockRepository.fetchStockNews(ticker)
                newsItems.postValue(news)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

    }



    fun loadHomePage() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val wallet = async { stockRepository.fetchWallet() }
                val portfolio = async { stockRepository.fetchPortfolio() }
                val watchlist = async { stockRepository.fetchWatchlist() }

                // Wait for all requests to complete
                walletData.postValue(wallet.await())
                portfolioItems.postValue(portfolio.await())
                watchlistItems.postValue(watchlist.await())

                val itemsTotal = portfolioItems.value?.sumOf { it.quantity * it.c } ?: 0.0
                val walletTotal = walletData.value?.balance ?: 0.0
                Log.d("CHECK1#:", itemsTotal.toString())
                Log.d("CHECK2#:", walletTotal.toString())
                val itemsTotal2 = portfolio.await().sumOf { it.quantity * it.c }
                val walletTotal2 = wallet.await().balance
                Log.d("CHECK1#:", itemsTotal2.toString())
                Log.d("CHECK2#:", walletTotal2.toString())
                _netWorth.postValue(itemsTotal2 + walletTotal2)

                // Data loaded, update loading state
                _isLoading.postValue(false)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
                _isLoading.postValue(false)
            }
        }
    }

    fun loadWallet() {
        viewModelScope.launch {
            try {
                val wallet = stockRepository.fetchWallet()
                walletData.postValue(wallet)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }
    }

    fun loadPortfolio() {
        viewModelScope.launch {
            try {
                val portfolio = stockRepository.fetchPortfolio()
                portfolioItems.postValue(portfolio)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

    }

    fun loadWatchlist() {
        viewModelScope.launch {
            try {
                val watchlist = stockRepository.fetchWatchlist()
                watchlistItems.postValue(watchlist)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }

    }

    fun calculateWorth() {
        val itemsTotal = portfolioItems.value?.sumOf { it.quantity * it.c } ?: 0.0
        Log.d("CHECK1#:", portfolioItems.value.toString())
        Log.d("CHECK2#:", walletData.value.toString())
        val walletTotal = walletData.value?.balance ?: 0.0
        _netWorth.postValue(itemsTotal + walletTotal)
    }

    fun loadAutoComplete(ticker: String) {
        viewModelScope.launch {
            try {
                val autoComplete = stockRepository.fetchAutoComplete(ticker)
                autoCompleteItems.postValue(autoComplete)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }
    }


    fun loadStockState(ticker: String) {
        viewModelScope.launch {
            try {
                val stateData = stockRepository.fetchStockState(ticker)
                stockState.postValue(stateData)
            } catch (e: Exception) {
                error.postValue(e.message ?: "Unknown error")
            }
        }
    }


}
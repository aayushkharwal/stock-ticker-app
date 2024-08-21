package repository

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.tickerappxml.MainActivity
import com.example.tickerappxml.MySingleton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.AutoComplete
import models.Favorite
import models.News
import models.Portfolio
import models.PortfolioDB
import models.Quote
import models.Sentiments
import models.StockProfile
import models.StockState
import models.TransactionDetails
import models.Wallet
import models.Watchlist
import models.WatchlistDB
import org.json.JSONException
import org.json.JSONObject

class StockRepository() {

    suspend fun fetchStockProfile(ticker: String): StockProfile = withContext(Dispatchers.IO) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_profile"
        val request = CompletableDeferred<StockProfile>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val profile = StockProfile(
                        country = response.getString("country"),
                        currency = response.getString("currency"),
                        estimateCurrency = response.getString("estimateCurrency"),
                        exchange = response.getString("exchange"),
                        finnhubIndustry = response.getString("finnhubIndustry"),
                        ipo = response.getString("ipo"),
                        logo = response.getString("logo"),
                        marketCapitalization = response.getString("marketCapitalization"),
                        name = response.getString("name"),
                        phone = response.getString("phone"),
                        shareOutstanding = response.getDouble("shareOutstanding"),
                        ticker = response.getString("ticker"),
                        weburl = response.getString("weburl"),
                    )
                    request.complete(profile)
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing profile data"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to load profile: ${error.message}"))
            }
        )

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }

    fun fetchStockQuote(ticker: String, onSuccess: (Quote) -> Unit, onError: (String) -> Unit) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_quote"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val quote = Quote(
                        c = response.getDouble("c"),
                        d = response.getDouble("d"),
                        dp = response.getDouble("dp"),
                        h = response.getDouble("h"),
                        l = response.getDouble("l"),
                        o = response.getDouble("o"),
                        pc = response.getDouble("pc"),
                        t = response.getString("t"),
                        unix_t = response.getInt("unix_t")
                    )
                    onSuccess(quote)
                } catch (e: JSONException) {
                    onError(e.localizedMessage ?: "Error parsing quote data") // Error parsing JSON
                }
            },
            { error ->
                onError(error.localizedMessage ?: "Error fetching quote data") // Network error or request failed
            }
        )

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
    }

    suspend fun fetchStockQuote2(ticker: String): Quote = withContext(Dispatchers.IO)  {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_quote"
        val request = CompletableDeferred<Quote>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val quote = Quote(
                        c = response.getDouble("c"),
                        d = response.getDouble("d"),
                        dp = response.getDouble("dp"),
                        h = response.getDouble("h"),
                        l = response.getDouble("l"),
                        o = response.getDouble("o"),
                        pc = response.getDouble("pc"),
                        t = response.getString("t"),
                        unix_t = response.getInt("unix_t")
                    )
                    request.complete(quote)
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing quote data"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to load quote: ${error.message}"))
            }
        )

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }

    suspend fun fetchStockSentiment(ticker: String): Sentiments = withContext(Dispatchers.IO) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_sentiment"
        val request = CompletableDeferred<Sentiments>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val sentiments = Sentiments(
                        mspr_total = response.getDouble("mspr_total"),
                        mspr_positive = response.getDouble("mspr_positive"),
                        mspr_negative = response.getDouble("mspr_negative"),
                        change_total = response.getDouble("change_total"),
                        change_positive = response.getDouble("change_positive"),
                        change_negative = response.getDouble("change_negative")
                    )
                    request.complete(sentiments)
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing sentiments data"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to load sentiments: ${error.message}"))
            }
        )

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }

    suspend fun fetchStockPeers(ticker: String): List<String> = withContext(Dispatchers.IO) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_peers"
        val request = CompletableDeferred<List<String>>()

        Log.d("Peers: ", url)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("Peers: ", response.toString())
                try {
                    val peers = mutableListOf<String>()
                    for (i in 0 until response.length()) {
                        peers.add(response.getString(i))
                    }
                    request.complete(peers)
                } catch (e: Exception) {
                    request.completeExceptionally(Exception("Error parsing peers data"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to load peers: ${error.toString()}"))
            })

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            30000, // Timeout in milliseconds
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        )

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonArrayRequest)
        request.await()
    }

    suspend fun fetchStockState(ticker: String): StockState = withContext(Dispatchers.IO) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/db/$ticker"
        val request = CompletableDeferred<StockState>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
            Log.d("DB data", response.toString())
                try {

                    val watchlistItem = response.getJSONObject("watchlist")
                    val watchlist = WatchlistDB(
                        status = watchlistItem.getBoolean("status")
                    )

                    val portfolioItem = response.getJSONObject("portfolio")
                    val portfolio = PortfolioDB(
                        ticker = portfolioItem.getString("_id"),
                        quantity = portfolioItem.getInt("quantity"),
                        totalCost = portfolioItem.getDouble("totalCost")
                    )

                    val stockState = StockState(watchlist, portfolio)
                    request.complete(stockState)
                } catch (e: Exception) {
                    request.completeExceptionally(Exception("Error parsing db data"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to db data: ${error.message}"))
            })

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }

    suspend fun fetchStockNews(ticker: String): List<News> = withContext(Dispatchers.IO) {
        val newsUrl = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker/stock_news"
        val request = CompletableDeferred<List<News>>()

        val newsRequest = JsonArrayRequest(Request.Method.GET, newsUrl, null,
            { response ->
                val newsItems = mutableListOf<News>()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val news = News(
                        datetime = item.getString("datetime"),
                        headline = item.getString("headline"),
                        image = item.getString("image"),
                        url = item.getString("url"),
                        category = item.getString("category"),
                        source = item.getString("source"),
                        summary = item.getString("summary"),
                        related = item.getString("related"),
                        id = item.getInt("id"),
                        datetime_unix = item.getLong("datetime_unix")
                    )
                    newsItems.add(news)
                }
                request.complete(newsItems)
            },
            { error ->
                request.completeExceptionally(Exception("Failed to load news: ${error.message}"))
            })

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(newsRequest)
        request.await()
    }

    suspend fun updateWatchlist(ticker: String, name: String) = withContext(Dispatchers.IO)  {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/watchlist/${ticker}/update?companyName=${name}"
        val request = CompletableDeferred<Favorite>()

        // Create the request
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Parsing the JSON response
                    val status = Favorite(
                        acknowledged = response.getBoolean("acknowledged"),
//                        insertedId = response.getString("insertedId"),
                        status = response.getString("status")
                    )
                    request.complete(status)
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing favorite status"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to favorite status: ${error.message}"))
            }
        )

        // Add the request to the Volley queue
        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }

    suspend fun fetchWallet() = withContext(Dispatchers.IO)  {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/wallet/balance"
        val request = CompletableDeferred<Wallet>()

        // Create the request
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Parsing the JSON response
                    val wallet = Wallet(
                        balance = response.getDouble("balance")
                    )
                    request.complete(wallet)
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing wallet data"))
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to wallet profile: ${error.message}"))
            }
        )

        // Add the request to the Volley queue
        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }

    suspend fun fetchPortfolio() = withContext(Dispatchers.IO) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/portfolio/all"
        val request = CompletableDeferred<List<Portfolio>>()

        // Create the request
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Parsing the JSON array response
                    val portfolioItems = mutableListOf<Portfolio>()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val portfolio = Portfolio(
                            name = item.getString("name"),
                            quantity = item.getInt("quantity"),
                            totalCost = item.getDouble("totalCost"),
                            c = item.getDouble("c"),
                            tickerSymbol = item.getString("tickerSymbol")
                        )
                        portfolioItems.add(portfolio)
                    }
                    request.complete(portfolioItems) // Invoke the success callback with the parsed news items
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing portfolio data")) // Error parsing JSON
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to portfolio profile: ${error.message}")) // Network error or request failed
            }
        )

        // Add the request to the Volley queue
        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonArrayRequest)
        request.await()
    }

    suspend fun fetchWatchlist() = withContext(Dispatchers.IO)  {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/watchlist/all"
        val request = CompletableDeferred<MutableList<Watchlist>>()

        // Create the request
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Parsing the JSON array response
                    val watchlistItems = mutableListOf<Watchlist>()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val watchlist = Watchlist(
                            name = item.getString("name"),
                            d = item.getDouble("d"),
                            dp = item.getDouble("dp"),
                            c = item.getDouble("c"),
                            ticker = item.getString("ticker")
                        )
                        watchlistItems.add(watchlist)
                    }
                    request.complete(watchlistItems) // Invoke the success callback with the parsed news items
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing watchlist data")) // Error parsing JSON
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to fetch watchlist: ${error.message}"))
            }
        )

        // Add the request to the Volley queue
        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonArrayRequest)
        request.await()
    }


    suspend fun postTransaction(transactionDetails: TransactionDetails, action: String) = withContext(Dispatchers.IO) {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/portfolio/update"
        val request = CompletableDeferred<TransactionDetails>()

        Log.d("BBBB3", transactionDetails.toString())

        val requestBody = JSONObject().apply {
            put("action", action)
            put("ticker", transactionDetails.ticker)
            put("name", transactionDetails.name)
            put("quantity", transactionDetails.quantity)
            put("totalCost", transactionDetails.totalCost)
            put("balance", transactionDetails.balance)
            put("currentPrice", transactionDetails.currentPrice)
        }

        Log.d("BBBB3", requestBody.toString())
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                try {
                    // Parsing the JSON array response
                    val transactionResponse = TransactionDetails(
                        quantity = response.getInt("quantity"),
                        totalCost = response.getDouble("totalCost"),
                        ticker = response.getString("ticker"),
                        name = response.getString("name"),
                        currentPrice = response.getDouble("currentPrice"),
                        balance = response.getDouble("balance")
                    )
                    request.complete(transactionResponse) // Invoke the success callback with the parsed news items
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing transaction post data")) // Error parsing JSON
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to post transaction profile: ${error.message}")) // Network error or request failed
            }
        )

        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonObjectRequest)
        request.await()
    }


//    fun postTransaction(transactionDetails: TransactionDetails, action: String, completionHandler: (Boolean, TransactionDetails) -> Unit) {
//        val url = "https://web-tech4-csci571.wl.r.appspot.com/portfolio/update"
//
//        Log.d("BBBB3", transactionDetails.toString())
//
//
//
//        val requestBody = JSONObject().apply {
//            put("action", action)
//            put("ticker", transactionDetails.ticker)
//            put("name", transactionDetails.name)
//            put("quantity", transactionDetails.quantity)
//            put("totalCost", transactionDetails.totalCost)
//            put("balance", transactionDetails.balance)
//            put("currentPrice", transactionDetails.currentPrice)
//        }.toString()
//
//        Log.d("BBBB3", requestBody)
//
//        val stringRequest = object : StringRequest(
//            Method.POST, url,
//            Response.Listener<String> { response ->
//                // Handle response from server
//                Log.d("Response from posting portfolio request BBBB", response.toString())
//                val jsonResponse = JSONObject(response)
//                val transactionResponse = TransactionDetails(
//                    quantity = jsonResponse.getInt("quantity"),
//                    totalCost = jsonResponse.getDouble("totalCost"),
//                    ticker = jsonResponse.getString("ticker"),
//                    name = jsonResponse.getString("name"),
//                    currentPrice = jsonResponse.getDouble("currentPrice"),
//                    balance = jsonResponse.getDouble("balance")
//                )
//                completionHandler(true, transactionResponse)
//            },
//            Response.ErrorListener { error ->
//                // Handle error
//                Log.d("Error posting portfolio request", error.toString())
//                completionHandler(false, transactionDetails)
//            }) {
//            override fun getBodyContentType(): String {
//                return "application/json; charset=utf-8"
//            }
//
//            override fun getBody(): ByteArray {
//                return requestBody.toByteArray(Charsets.UTF_8)
//            }
//        }
//        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(stringRequest)
//    }

    suspend fun fetchAutoComplete(ticker: String) = withContext(Dispatchers.IO)  {
        val url = "https://web-tech4-csci571.wl.r.appspot.com/search/$ticker"
        val request = CompletableDeferred<MutableList<AutoComplete>>()

        // Create the request
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("Fetched AutoComplete: ", response.toString())
                try {
                    // Parsing the JSON array response
                    val autoCompleteItems = mutableListOf<AutoComplete>()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val autoCompleteItem = AutoComplete(
                            description = item.getString("description"),
                            displaySymbol = item.getString("displaySymbol"),
                            symbol = item.getString("symbol"),
                            type = item.getString("type")
                        )
                        autoCompleteItems.add(autoCompleteItem)
                    }
                    request.complete(autoCompleteItems) // Invoke the success callback with the parsed news items
                } catch (e: JSONException) {
                    request.completeExceptionally(Exception("Error parsing autoComplete data")) // Error parsing JSON
                }
            },
            { error ->
                request.completeExceptionally(Exception("Failed to fetch AutoComplete: ${error.toString()}"))
            }
        )

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            30000, // Timeout in milliseconds
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        )

        // Add the request to the Volley queue
        MySingleton.getInstance(MainActivity.appContext).addToRequestQueue(jsonArrayRequest)
        request.await()
    }



}
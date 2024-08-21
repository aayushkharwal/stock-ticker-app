package models


data class StockState(
    val watchlist: WatchlistDB,
    val portfolio: PortfolioDB
)

data class WatchlistDB(
    val status: Boolean
)

data class PortfolioDB(
    val quantity: Int,
    val totalCost: Double,
    val ticker: String
)


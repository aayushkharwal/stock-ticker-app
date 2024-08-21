package models

data class StockTransaction(
    val quantity: Int,
    val totalCost: Double,
    val ticker: String,
    val balance: Double,
    val name: String,
    val currentPrice: Double
)

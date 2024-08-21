package models

data class Portfolio (
    val name: String,
    val quantity: Int,
    val totalCost: Double,
    val c: Double,
    val tickerSymbol: String
)
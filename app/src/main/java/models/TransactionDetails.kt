package models

data class TransactionDetails(
//    val action: String,
    val ticker: String,
    val name: String,
    val quantity: Int,
    val totalCost: Double,
    val balance: Double,
    val currentPrice: Double
)


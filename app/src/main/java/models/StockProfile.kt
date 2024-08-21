package models

data class StockProfile(
    val country: String,
    val currency: String,
    val estimateCurrency: String,
    val exchange: String,
    val finnhubIndustry: String,
    val ipo: String,
    val logo: String,
    val marketCapitalization: String,
    val name: String,
    val phone: String,
    val shareOutstanding: Double,
    val ticker: String,
    val weburl: String

)

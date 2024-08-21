package models

data class Sentiments(
    val mspr_total: Double,
    val mspr_positive: Double,
    val mspr_negative: Double,
    val change_total: Double,
    val change_positive: Double,
    val change_negative: Double
)
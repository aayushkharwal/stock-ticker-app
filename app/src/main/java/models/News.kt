package models

data class News(
    val datetime_unix: Long,
    val datetime: String,
    val headline: String,
    val image: String,
    val url: String,
    val category: String,
    val source: String,
    val summary: String,
    val related: String,
    val id: Int
)

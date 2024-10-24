package models


// News would have been a better name fot this data class :)
data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)
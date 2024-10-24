package repository

import androidx.lifecycle.LiveData
import api.RetrofitInstance
import db.ArticleDatabase
import models.Article
import models.NewsResponse
import retrofit2.Response

class NewsRepository(
    val db:ArticleDatabase
) {

    suspend fun getBreakingNews(
        countryCode: String,
        pageNumber:Int
    ):Response<NewsResponse>{
        return RetrofitInstance.api.getBreakingNews(
            countryCode,
            pageNumber
        )
    }
    suspend fun searchNews(
        searchQuery:String,
        pageNumber: Int
    ):Response<NewsResponse>{
        return RetrofitInstance.api.searchForNews(
            searchQuery, pageNumber
        )
    }

    suspend fun insert(article: Article): Long {
        return db.getArticleDao().insert(article)
    }

    fun getSavedNews():LiveData<List<Article>>{
        return db.getArticleDao().getAllArticles()
    }

    suspend fun deleteArticle(article: Article){
        db.getArticleDao().deleteArticle(article)
    }
}
package com.example.newsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.EMPTY_STRING_ARRAY
import kotlinx.coroutines.launch
import models.Article
import models.NewsResponse
import repository.NewsRepository
import retrofit2.Response
import util.Resource
import java.util.Locale.IsoCountryCode

class NewsViewModel(
    val newsRepository: NewsRepository
): ViewModel() {

    //Mutable live data for getting news
     private val _breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
     private val _searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var breakingNewsResponse:NewsResponse? = null

    //Read only data for observers (for fragment/activity to observe)
    val breakingNews:LiveData<Resource<NewsResponse>> = _breakingNews
    val searchNews:LiveData<Resource<NewsResponse>> = _searchNews
    /**
     * "breakingNewsPage" is for managing pagination in recycler view. we can do this in fragment as well but after
     * configuration change (device rotation) it will reset to 1 every time. Since, ViewModel survives configuration changes
     * it is better to do it here.
     */
     var breakingNewsPage = 1
     var searchNewsPage = 1
    var searchNewsResponse:NewsResponse? = null


    init {
        //this can be done in the respective fragment as well.
        getBreakingNews("us")
    }

    /**
     *  "viewModelScope" -This co-routine stays alive until the viewmodel is alive
     */
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        //Emit the loading state(class) from Resource
        _breakingNews.postValue(Resource.Loading())
        val response:Response<NewsResponse> = newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        _breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    /**
     * If the first response is successful then fetch another page of response (breakingNewsPage++)
     * then combine the old articles with new articles
     * At last if breakingNewsResponse is null then only return the first response i.e. resultResponse
     */
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                breakingNewsPage++
                if(breakingNewsResponse==null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun searchNews(searchQuery:String)=
        viewModelScope.launch {
            //Emit the loading state(class) from resource
            _searchNews.postValue(Resource.Loading())
            val response = newsRepository.searchNews(searchQuery,searchNewsPage)
            _searchNews.postValue(handleSearchNewsResponse(response))
        }


    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse->
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article){
        viewModelScope.launch {
            newsRepository.insert(article)
        }
    }

    fun getSavedNews():LiveData<List<Article>>{
        return newsRepository.getSavedNews()
    }

    fun deleteArticle(article: Article){
        viewModelScope.launch {
            newsRepository.deleteArticle(article)
        }
    }
}
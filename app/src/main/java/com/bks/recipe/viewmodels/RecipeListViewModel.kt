package com.bks.recipe.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.bks.recipe.models.Recipe
import com.bks.recipe.repositories.RecipeRepository
import com.bks.recipe.util.Constant.Companion.QUERY_EXHAUSTED
import com.bks.recipe.util.Resource
import com.bks.recipe.util.Status

class RecipeListViewModel constructor(private val recipeRepository : RecipeRepository) : ViewModel() {

    var viewState =  MutableLiveData<ViewState>().apply { ViewState.CATEGORIES }
    val recipes: MediatorLiveData<Resource<List<Recipe>>> = MediatorLiveData()

    // query extras
    private var isQueryExhausted = false
    private var isPerformingQuery = false
    var pageNumber = 0
    private var query: String = ""
    private var cancelRequest = false
    private var requestStartTime: Long = 0

    init {
        viewState.value = ViewState.CATEGORIES
    }

    fun searchRecipesApi(query: String, pageNumber: Int) {
        var pageNumber = pageNumber
        if (!isPerformingQuery) {
            if (pageNumber == 0) {
                pageNumber = 1
            }
            this.pageNumber = pageNumber
            this.query = query
            isQueryExhausted = false
            executeSearch()
        }
    }

    fun searchNextPage() {
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++
            executeSearch()
        }
    }

    private fun executeSearch() {
        requestStartTime = System.currentTimeMillis()
        cancelRequest = false
        isPerformingQuery = true
        viewState.value = ViewState.RECIPES

        val repositorySource: LiveData<Resource<List<Recipe>>> = recipeRepository.searchRecipesApi(query, pageNumber)
        Log.d("abk", "repositorySource : ${repositorySource.value}")
        recipes.addSource(repositorySource,
             Observer<Resource<List<Recipe>>> {listResource ->
                    if (!cancelRequest) {
                        if (listResource != null) {
                            if (listResource.status === Status.SUCCESS) {
                                Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.")
                                Log.d(TAG, "onChanged: page number: $pageNumber")
                                Log.d(TAG, "onChanged: " + listResource.data)

                                isPerformingQuery = false
                                if (listResource.data != null) {
                                    if (listResource.data.isEmpty()) {
                                        Log.d(TAG, "onChanged: query is exhausted...")
                                        recipes.value = Resource<List<Recipe>>(Status.ERROR, listResource.data, QUERY_EXHAUSTED)
                                        isQueryExhausted = true
                                    }
                                }
                                recipes.removeSource(repositorySource)

                            } else if (listResource.status == Status.ERROR) {
                                Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.")
                                isPerformingQuery = false
                                if (listResource.message.equals(QUERY_EXHAUSTED)) {
                                    isQueryExhausted = true
                                }
                                recipes.removeSource(repositorySource)
                            }
                            recipes.setValue(listResource)
                        } else {
                            recipes.removeSource(repositorySource)
                        }
                    } else {
                        recipes.removeSource(repositorySource)
                    }
            })
    }

    fun cancelSearchRequest() {
        if (isPerformingQuery) {
            Log.d(TAG, "cancelSearchRequest: canceling the search request.")
            cancelRequest = true
            isPerformingQuery = false
            pageNumber = 1
        }
    }

    fun setViewCategories() {
        viewState.value = ViewState.CATEGORIES
    }

    companion object {
        private const val TAG = "RecipeListViewModel"
    }
}

enum class ViewState {CATEGORIES, RECIPES}
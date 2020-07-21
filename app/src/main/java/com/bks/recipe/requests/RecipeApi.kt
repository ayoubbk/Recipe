package com.bks.recipe.requests

import androidx.lifecycle.LiveData
import com.bks.recipe.requests.response.ApiResponse
import com.bks.recipe.requests.response.RecipeResponse
import com.bks.recipe.requests.response.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {

    // SEARCH
    @GET("api/search")
    fun searchRecipe(
        @Query("key") key: String?,
        @Query("q") query: String?,
        @Query("page") page: String?
    ): LiveData<ApiResponse<RecipeSearchResponse?>>

    // GET RECIPE REQUEST
    @GET("api/get")
    fun getRecipe(
        @Query("key") key: String?,
        @Query("rId") recipe_id: String?
    ): LiveData<ApiResponse<RecipeResponse>>
}
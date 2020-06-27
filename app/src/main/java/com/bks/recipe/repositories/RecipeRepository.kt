package com.bks.recipe.repositories

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.lifecycle.LiveData
import com.bks.recipe.executor.AppExecutors
import com.bks.recipe.models.Recipe
import com.bks.recipe.persistence.RecipeDao
import com.bks.recipe.persistence.RecipeDatabase
import com.bks.recipe.requests.response.ApiResponse
import com.bks.recipe.requests.response.RecipeResponse
import com.bks.recipe.requests.response.RecipeSearchResponse
import com.bks.recipe.util.Constant
import com.bks.recipe.util.NetworkBoundResource
import com.bks.recipe.util.Resource

class RecipeRepository private constructor(context: Context) {
    private val recipeDao: RecipeDao = RecipeDatabase.getInstance(context).getRecipeDao() // val mean readOnly

    fun searchRecipesApi(query: String, pageNumber: Int): LiveData<Resource<List<Recipe>>> {
        return object :
            NetworkBoundResource<List<Recipe>, RecipeSearchResponse?>(AppExecutors.getInstance()!!) {

            override fun saveCallResult(item: RecipeSearchResponse?) {
                if (item?.recipes != null) { // recipe list will be null if the api key is expired
//                    Log.d(TAG, "saveCallResult: recipe response: " + item.toString());
//                    val recipes: Array<Recipe?> = arrayOfNulls<Recipe>(item.recipes.size)
//
//                    var index = 0
//                    for (rowid in recipeDao.insertRecipes(
//                        item.recipes.toArray(recipes) as Array<Recipe?>
//                    )) {
//                        if (rowid == -1L) {
//                            Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in the cache")
//                            // if the recipe already exists... I don't want to set the ingredients or timestamp b/c
//                            // they will be erased
//                            recipeDao.updateRecipe(
//                                recipes[index]?.id,
//                                recipes[index]?.title,
//                                recipes[index].getPublisher(),
//                                recipes[index].getImage_url(),
//                                recipes[index].getSocial_rank()
//                            )
//                        }
//                        index++
//                    }
                }
            }

            override fun shouldFetch(data: List<Recipe>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Recipe>> {
                return recipeDao.searchRecipes(query, pageNumber)
            }

            // creating LiveData Retrofit object
            // LiveData is executed asynchronously , the retrofit call object will automatically be done on the
            // background thread, I won't use Executors.
            override fun createCall(): LiveData<ApiResponse<RecipeSearchResponse?>> {
                return ServiceGenerator.getRecipeApi()
                    .searchRecipe(Constant.API_KEY, query, pageNumber.toString())
            }
        }.asLiveData()
    }

    fun searchRecipesApi(recipeId: String?): LiveData<Resource<Recipe>> {
        return object : NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.getInstance()) {
            fun saveCallResult(@NonNull item: RecipeResponse) {

                // will be null if API key is expired
                if (item.getRecipe() != null) {
                    item.getRecipe()
                        .setTimestamp((System.currentTimeMillis() / 1000).toInt())
                    recipeDao.insertRecipe(item.getRecipe())
                }
            }

            fun shouldFetch(@Nullable data: Recipe): Boolean {
                Log.d(
                    TAG,
                    "shouldFetch: recipe: " + data.toString()
                )
                val currentTime = (System.currentTimeMillis() / 1000).toInt()
                Log.d(
                    TAG,
                    "shouldFetch: current time: $currentTime"
                )
                val lastRefresh: Int = data.getTimestamp()
                Log.d(
                    TAG,
                    "shouldFetch: last refresh: $lastRefresh"
                )
                Log.d(
                    TAG,
                    "shouldFetch: it's been " + (currentTime - lastRefresh) / 60 / 60 / 24 +
                            " days since this recipe was refreshed. 30 days must elapse before refreshing. "
                )
                if (currentTime - data.getTimestamp() >= Constant.RECIPE_REFRESH_TIME) {
                    Log.d(
                        TAG,
                        "shouldFetch: SHOULD REFRESH RECIPE?! " + true
                    )
                    return true
                }
                Log.d(
                    TAG,
                    "shouldFetch: SHOULD REFRESH RECIPE?! " + false
                )
                return false
            }

            @NonNull
            fun loadFromDb(): LiveData<Recipe> {
                return recipeDao.getRecipe(recipeId)
            }

            @NonNull
            fun createCall(): LiveData<ApiResponse<RecipeResponse>> {
                return ServiceGenerator.getRecipeApi().getRecipe(
                    Constant.API_KEY,
                    recipeId
                )
            }
        }.asLiveData()
    }

    companion object {
        private const val TAG = "RecipeRepository"

        @Volatile private var instance: RecipeRepository? = null

        fun getInstance(context: Context): RecipeRepository? {
            if (instance == null) { // check 1
                synchronized(this) {
                    if(instance == null) // check 2
                    instance = RecipeRepository(context)
                }
            }
            return instance
        }
    }

}
